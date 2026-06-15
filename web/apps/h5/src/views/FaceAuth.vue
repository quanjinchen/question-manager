<template>
  <main class="face-auth-page">
    <video
      ref="videoRef"
      class="camera-video"
      autoplay
      muted
      playsinline
      webkit-playsinline
    />

    <div class="white-mask" />

    <section class="auth-stage">
      <div class="guide">
        <h1>{{ titleText }}</h1>
        <p>{{ statusText }}</p>
        <p
          v-if="isRegisterMode"
          class="register-tip"
        >
          请去光源充足的地方录取人脸底图，这会影响后续认证的质量
        </p>
      </div>

      <div
        class="capture-ring"
        :style="ringStyle"
      >
        <div class="scanner-line" />
        <div class="ring-glow" />
        <div
          v-if="loading"
          class="loading-state"
        >
          <van-loading
            color="#1f6feb"
            size="24px"
          />
        </div>
      </div>
     

      <div class="action-area">
        <van-button
          v-if="errorMessage"
          block
          type="primary"
          round
          :loading="restartLoading"
          @click="dataInfo.restart()"
        >
          重新开始
        </van-button>
      </div>
    </section>

    <canvas
      ref="canvasRef"
      class="capture-canvas"
    />
  </main>
</template>

<script setup lang="ts">
import { reactive, toRefs, onBeforeUnmount, onMounted, ref, watchEffect } from 'vue';
import { showFailToast } from 'vant';
import { useRoute, useRouter } from 'vue-router';
import * as faceapi from 'face-api.js';
import { $apis } from '@/apis/requests';
import {
  clearCurrentAuthAccount,
  getCurrentAuthAccount,
  setCurrentAuthAccount,
  type CachedAuthAccount
} from '@/apis/auth-session';

const MODEL_URL = '/models/face-api';
const LANDMARK_MODEL_MANIFEST = `${MODEL_URL}/face_landmark_68_tiny_model-weights_manifest.json`;
const captureTotal = 3;
const captureDelay = 900;
const progressAnimationDuration = 1040;
const faceReadyScoreThreshold = 0.65;
const blinkCloseDropRatio = 0.06;
const blinkCloseDropValue = 0.014;
const blinkOpenRecoverRatio = 0.03;
const blinkOpenRecoverValue = 0.009;
type AuthStep = 'face' | 'blink' | 'capture' | 'submit' | 'done';

const router = useRouter();
const route = useRoute();
const videoRef = ref<HTMLVideoElement>();
const canvasRef = ref<HTMLCanvasElement>();
let stream: MediaStream | null = null;
let frameTimer = 0;
let progressAnimationFrame = 0;
let submitTimer = 0;

const dataInfo = reactive({
  captures: [] as string[],
  loading: true,
  submitting: false,
  restartLoading: false,
  detecting: false,
  errorMessage: '',
  statusText: '正在初始化摄像头',
  authStep: 'face' as AuthStep,
  lastGuideUpdateTime: 0,
  lastCaptureTime: 0,
  lastScoreLogTime: 0,
  progressDeg: 0,
  captureProgressTargets: [] as number[],
  landmarkReady: false,
  livenessPassed: false,
  blinkClosing: false,
  blinkBaselineEyeRatio: 0,
  blinkMinEyeRatio: 0,
  account: null as CachedAuthAccount | null,
  get isRegisterMode() {
    return route.query.mode === 'register' || Boolean(this.account?.registerMode);
  },
  get navTitle() {
    return this.isRegisterMode ? '人脸注册' : '人脸认证';
  },
  get titleText() {
    if (this.errorMessage) {
      return '认证暂不可用';
    }
    if (this.submitting) {
      return this.isRegisterMode ? '正在注册' : '正在认证';
    }
    if (this.captures.length >= captureTotal) {
      return '采集完成';
    }
    const titleMap: Record<AuthStep, string> = {
      face: '请正脸进入镜头',
      blink: '请正脸进入镜头并眨眼',
      capture: '正在采集',
      submit: this.isRegisterMode ? '正在注册' : '正在认证',
      done: '采集完成'
    };
    if (this.landmarkReady) {
      return titleMap[this.authStep as AuthStep];
    }
    return this.isRegisterMode ? '请采集本人人脸' : '请将脸部置于圆框内';
  },
  get ringStyle() {
    return {
      '--capture-progress': `${this.progressDeg}deg`
    };
  },
  createCaptureProgressTargets() {
    const weights = Array.from({ length: captureTotal }, () => 0.72 + Math.random() * 0.56);
    const totalWeight = weights.reduce((total, weight) => total + weight, 0);
    let accumulated = 0;

    this.captureProgressTargets = weights.map((weight, index) => {
      if (index === weights.length - 1) {
        return 360;
      }
      accumulated += (weight / totalWeight) * 360;
      return Math.round(accumulated);
    });
  },
  async loadModels() {
    this.setGuideStatus('正在加载人脸模型', true);
    await faceapi.nets.tinyFaceDetector.loadFromUri(MODEL_URL);
    await this.loadLandmarkModel();
  },
  async loadLandmarkModel() {
    try {
      const response = await fetch(LANDMARK_MODEL_MANIFEST, { method: 'HEAD' });
      if (!response.ok) {
        console.warn('[face-api] 未找到 landmark 模型，正脸检测未启用');
        this.landmarkReady = false;
        return;
      }

      await faceapi.nets.faceLandmark68TinyNet.loadFromUri(MODEL_URL);
      this.landmarkReady = true;
      console.log('[face-api] landmark 模型已加载，正脸检测已启用');
    } catch {
      console.warn('[face-api] landmark 模型加载失败，正脸检测未启用');
      this.landmarkReady = false;
    }
  },
  async startCamera() {
    this.setGuideStatus('正在打开摄像头', true);
    const videoConstraints: MediaTrackConstraints & { resizeMode?: string } = {
      facingMode: 'user',
      width: { ideal: 1280 },
      height: { ideal: 720 },
      aspectRatio: { ideal: 16 / 9 },
      resizeMode: 'none'
    };

    stream = await navigator.mediaDevices.getUserMedia({
      audio: false,
      video: videoConstraints
    });

    if (!videoRef.value) {
      return;
    }

    const videoTrack = stream.getVideoTracks()[0];
    const capabilities = videoTrack?.getCapabilities?.() as MediaTrackCapabilities & {
      zoom?: { min?: number };
    };
    if (capabilities?.zoom?.min !== undefined) {
      await videoTrack.applyConstraints({
        advanced: [{ zoom: capabilities.zoom.min } as MediaTrackConstraintSet]
      });
    }

    videoRef.value.srcObject = stream;
    await videoRef.value.play();
  },
  setAuthStep(step: AuthStep) {
    if (this.authStep === step) {
      return;
    }
    this.authStep = step;
  },
  setGuideStatus(text: string, force = false) {
    const now = Date.now();
    if (!force && this.statusText === text) {
      return;
    }
    if (!force && now - this.lastGuideUpdateTime < 900) {
      return;
    }
    this.statusText = text;
    this.lastGuideUpdateTime = now;
  },
  isFaceReady(detection: faceapi.FaceDetection, landmarks?: faceapi.FaceLandmarks68) {
    const video = videoRef.value;
    if (!video) {
      return false;
    }

    const { box, score } = detection;
    const centerX = box.x + box.width / 2;
    const centerY = box.y + box.height / 2;
    const videoCenterX = video.videoWidth / 2;
    const videoCenterY = video.videoHeight / 2;
    const offsetX = Math.abs(centerX - videoCenterX) / video.videoWidth;
    const offsetY = Math.abs(centerY - videoCenterY) / video.videoHeight;
    const faceRatio = box.width / video.videoWidth;

    return (
      score > faceReadyScoreThreshold
      && offsetX < 0.2
      && offsetY < 0.22
      && faceRatio > 0.22
      && faceRatio < 0.7
      && this.isFrontalFace(detection, landmarks)
    );
  },
  getPointCenter(points: faceapi.Point[]) {
    const total = points.reduce((result, point) => ({
      x: result.x + point.x,
      y: result.y + point.y
    }), { x: 0, y: 0 });
    return {
      x: total.x / points.length,
      y: total.y / points.length
    };
  },
  isFrontalFace(detection: faceapi.FaceDetection, landmarks?: faceapi.FaceLandmarks68) {
    if (!this.landmarkReady || !landmarks) {
      return true;
    }

    const leftEye = this.getPointCenter(landmarks.getLeftEye());
    const rightEye = this.getPointCenter(landmarks.getRightEye());
    const nose = landmarks.getNose()[3] ?? this.getPointCenter(landmarks.getNose());
    const mouth = landmarks.getMouth();
    const leftMouth = mouth[0];
    const rightMouth = mouth[6];
    const mouthCenter = this.getPointCenter([leftMouth, rightMouth]);
    const faceWidth = detection.box.width;
    const eyeCenterX = (leftEye.x + rightEye.x) / 2;
    const eyeLevelDiff = Math.abs(leftEye.y - rightEye.y) / faceWidth;
    const mouthLevelDiff = Math.abs(leftMouth.y - rightMouth.y) / faceWidth;
    const noseOffset = Math.abs(nose.x - eyeCenterX) / faceWidth;
    const mouthOffset = Math.abs(mouthCenter.x - nose.x) / faceWidth;
    const leftEyeToNose = Math.abs(nose.x - leftEye.x);
    const rightEyeToNose = Math.abs(rightEye.x - nose.x);
    const eyeNoseBalance = Math.abs(leftEyeToNose - rightEyeToNose) / Math.max(leftEyeToNose, rightEyeToNose, 1);
    const frontal = eyeLevelDiff < 0.08 && mouthLevelDiff < 0.1 && noseOffset < 0.12 && mouthOffset < 0.12 && eyeNoseBalance < 0.24;

    console.log('[face-api] 正脸检测', {
      frontal,
      eyeLevelDiff: Number(eyeLevelDiff.toFixed(4)),
      mouthLevelDiff: Number(mouthLevelDiff.toFixed(4)),
      noseOffset: Number(noseOffset.toFixed(4)),
      mouthOffset: Number(mouthOffset.toFixed(4)),
      eyeNoseBalance: Number(eyeNoseBalance.toFixed(4))
    });

    return frontal;
  },
  getEyeAspectRatio(points: faceapi.Point[]) {
    const verticalLeft = Math.hypot(points[1].x - points[5].x, points[1].y - points[5].y);
    const verticalRight = Math.hypot(points[2].x - points[4].x, points[2].y - points[4].y);
    const horizontal = Math.hypot(points[0].x - points[3].x, points[0].y - points[3].y);
    return (verticalLeft + verticalRight) / (2 * Math.max(horizontal, 1));
  },
  updateLiveness(landmarks?: faceapi.FaceLandmarks68) {
    if (!this.landmarkReady || !landmarks) {
      return true;
    }
    if (this.livenessPassed) {
      return true;
    }

    const leftEyeRatio = this.getEyeAspectRatio(landmarks.getLeftEye());
    const rightEyeRatio = this.getEyeAspectRatio(landmarks.getRightEye());
    const eyeRatio = (leftEyeRatio + rightEyeRatio) / 2;
    this.blinkBaselineEyeRatio = Math.max(this.blinkBaselineEyeRatio, eyeRatio);
    const eyeDrop = this.blinkBaselineEyeRatio - eyeRatio;
    const eyeDropRatio = eyeDrop / Math.max(this.blinkBaselineEyeRatio, 1);
    const isEyeClosed = eyeDropRatio >= blinkCloseDropRatio || eyeDrop >= blinkCloseDropValue;
    const recoverValue = eyeRatio - this.blinkMinEyeRatio;
    const recoverRatio = recoverValue / Math.max(this.blinkBaselineEyeRatio, 1);
    const isEyeReopened = recoverRatio >= blinkOpenRecoverRatio || recoverValue >= blinkOpenRecoverValue;

    console.log('[face-api] 活体眨眼检测', {
      passed: this.livenessPassed,
      closing: this.blinkClosing,
      eyeRatio: Number(eyeRatio.toFixed(4)),
      baseline: Number(this.blinkBaselineEyeRatio.toFixed(4)),
      minEyeRatio: Number(this.blinkMinEyeRatio.toFixed(4)),
      eyeDrop: Number(eyeDrop.toFixed(4)),
      eyeDropRatio: Number(eyeDropRatio.toFixed(4)),
      recoverValue: Number(recoverValue.toFixed(4)),
      recoverRatio: Number(recoverRatio.toFixed(4))
    });

    if (!this.blinkClosing && isEyeClosed) {
      this.blinkClosing = true;
      this.blinkMinEyeRatio = eyeRatio;
      this.setGuideStatus('已检测到闭眼，请睁开眼睛', true);
      return false;
    }

    if (this.blinkClosing) {
      this.blinkMinEyeRatio = Math.min(this.blinkMinEyeRatio || eyeRatio, eyeRatio);
    }

    if (this.blinkClosing && isEyeReopened) {
      this.livenessPassed = true;
      this.setAuthStep('capture');
      this.setGuideStatus('活体检测通过，开始采集', true);
      return true;
    }

    this.setGuideStatus(this.blinkClosing ? '请睁开眼睛' : '请正脸进入镜头并眨眼');
    return false;
  },
  logFaceScore(detection: faceapi.FaceDetection) {
    if (Date.now() - this.lastScoreLogTime < 500) {
      return;
    }

    this.lastScoreLogTime = Date.now();
    const { box, score } = detection;
    console.log('[face-api] 人脸检测分数', {
      score: Number(score.toFixed(4)),
      box: {
        x: Math.round(box.x),
        y: Math.round(box.y),
        width: Math.round(box.width),
        height: Math.round(box.height)
      }
    });
  },
  captureFrame() {
    const video = videoRef.value;
    const canvas = canvasRef.value;
    if (!video || !canvas) {
      return;
    }

    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    const context = canvas.getContext('2d');
    if (!context) {
      return;
    }

    context.drawImage(video, 0, 0, canvas.width, canvas.height);
    this.captures.push(canvas.toDataURL('image/jpeg', 0.86));
    this.lastCaptureTime = Date.now();
    const targetDeg = this.captureProgressTargets[this.captures.length - 1] ?? 360;
    this.animateProgress(targetDeg, this.captures.length >= captureTotal ? 520 : progressAnimationDuration);

    if (this.captures.length >= captureTotal) {
      this.setAuthStep('done');
      this.setGuideStatus('已完成 3 张人脸图片采集', true);
      this.stopDetecting();
      submitTimer = window.setTimeout(() => dataInfo.submitFace(), 520);
      return;
    }

    this.setGuideStatus('正在采集，请保持正脸不动');
  },
  async detectLoop() {
    if (!this.detecting || !videoRef.value) {
      return;
    }

    try {
      const options = new faceapi.TinyFaceDetectorOptions({
        inputSize: 224,
        scoreThreshold: 0.55
      });
      let detection: faceapi.FaceDetection | undefined;
      let landmarks: faceapi.FaceLandmarks68 | undefined;
      if (this.landmarkReady) {
        const faceResult = await faceapi.detectSingleFace(videoRef.value, options).withFaceLandmarks(true);
        detection = faceResult?.detection;
        landmarks = faceResult?.landmarks;
      } else {
        detection = await faceapi.detectSingleFace(videoRef.value, options) ?? undefined;
      }

      if (!detection) {
        if (this.authStep === 'blink') {
          this.setGuideStatus('请正脸进入镜头并眨眼');
        } else {
          this.setAuthStep('face');
          this.setGuideStatus('请将脸部置于圆框内，并保持正脸');
        }
      } else {
        this.logFaceScore(detection);
        if (!this.isFaceReady(detection, landmarks)) {
          if (this.authStep === 'blink') {
            this.setGuideStatus('请正脸进入镜头并眨眼');
          } else {
            this.setAuthStep('face');
            this.setGuideStatus(this.landmarkReady ? '请正脸面向屏幕，保持在圆框内' : '请正对屏幕，并让脸部保持在圆框中央');
          }
        } else if (!this.updateLiveness(landmarks)) {
          this.setAuthStep('blink');
        } else if (Date.now() - this.lastCaptureTime > captureDelay) {
          this.setAuthStep('capture');
          this.captureFrame();
        } else {
          this.setAuthStep('capture');
          this.setGuideStatus('正在采集，请保持正脸不动');
        }
      }
    } catch {
      this.errorMessage = '人脸检测失败，请重试';
      this.statusText = '人脸检测失败，请重试';
      this.stopDetecting();
    }

    frameTimer = window.setTimeout(() => dataInfo.detectLoop(), 180);
  },
  stopDetecting() {
    this.detecting = false;
    window.clearTimeout(frameTimer);
    window.clearTimeout(submitTimer);
  },
  animateProgress(targetDeg: number, duration = progressAnimationDuration) {
    window.cancelAnimationFrame(progressAnimationFrame);
    const startDeg = this.progressDeg;
    const startTime = performance.now();
    const distance = Math.max(targetDeg - startDeg, 0);
    if (!distance) {
      return;
    }

    const tick = (time: number) => {
      const progress = Math.min((time - startTime) / duration, 1);
      const easedProgress = progress < 0.72
        ? progress
        : 1 - Math.pow(1 - progress, 2) * 0.28 / 0.0784;
      this.progressDeg = startDeg + (targetDeg - startDeg) * easedProgress;
      if (progress < 1) {
        progressAnimationFrame = window.requestAnimationFrame(tick);
        return;
      }
      this.progressDeg = targetDeg;
    };

    progressAnimationFrame = window.requestAnimationFrame(tick);
  },
  stopCamera() {
    stream?.getTracks().forEach(track => track.stop());
    stream = null;
  },
  async submitFace() {
    if (!this.account || this.submitting) {
      return;
    }

    this.submitting = true;
    this.loading = true;
    this.setAuthStep('submit');
    this.setGuideStatus(this.isRegisterMode ? '正在提交人脸注册信息' : '正在进行人脸比对', true);
    try {
      const params = {
        certToken: this.account.certToken,
        fullName: this.account.fullName,
        idCard: this.account.idCard,
        faceImageBase64: this.captures[0]
      };
      if (this.isRegisterMode) {
        await $apis.auth.registerAccount(params);
        setCurrentAuthAccount({ ...this.account, registerMode: false });
      } else {
        await $apis.auth.compareFace(params);
      }
      const resultMode = this.isRegisterMode ? 'register' : 'auth';
      clearCurrentAuthAccount();
      this.setGuideStatus(this.isRegisterMode ? '注册成功' : '认证成功', true);
      this.stopCamera();
      await router.replace({
        path: '/auth-result',
        query: { mode: resultMode }
      });
    } catch {
      if (!this.isRegisterMode) {
        this.stopCamera();
        await router.replace({
          path: '/auth-result',
          query: {
            mode: 'auth',
            status: 'fail',
            certToken: this.account.certToken
          }
        });
        return;
      }

      this.errorMessage = '人脸注册失败，请重试';
      this.statusText = this.errorMessage;
    } finally {
      this.submitting = false;
      this.loading = false;
    }
  },
  async start() {
    const token = route.query.certToken;
    const certToken = Array.isArray(token) ? token[0] || '' : token || '';
    this.account = getCurrentAuthAccount(certToken);
    if (!this.account) {
      this.errorMessage = '未找到认证账户信息';
      this.statusText = '请返回认证入口重新填写身份信息';
      showFailToast(this.errorMessage);
      return;
    }

    this.errorMessage = '';
    this.captures = [];
    this.lastCaptureTime = 0;
    this.lastGuideUpdateTime = 0;
    this.progressDeg = 0;
    this.createCaptureProgressTargets();
    this.authStep = 'face';
    this.livenessPassed = false;
    this.blinkClosing = false;
    this.blinkBaselineEyeRatio = 0;
    this.blinkMinEyeRatio = 0;
    this.loading = true;

    try {
      await this.loadModels();
      await this.startCamera();
      this.detecting = true;
      this.setGuideStatus('请将脸部置于圆框内，并保持正脸', true);
      this.detectLoop();
    } catch {
      this.errorMessage = '无法打开摄像头或加载模型';
      this.statusText = '请检查摄像头权限和网络连接';
      showFailToast(this.errorMessage);
      this.stopCamera();
    } finally {
      this.loading = false;
    }
  },
  async restart() {
    if (this.restartLoading) {
      return;
    }
    this.restartLoading = true;
    try {
      this.stopDetecting();
      this.stopCamera();
      await this.start();
    } finally {
      this.restartLoading = false;
    }
  }
});

onMounted(() => {
  dataInfo.start();
});

watchEffect(() => {
  document.title = dataInfo.navTitle;
});

onBeforeUnmount(() => {
  window.cancelAnimationFrame(progressAnimationFrame);
  window.clearTimeout(submitTimer);
  dataInfo.stopDetecting();
  dataInfo.stopCamera();
});

const {
  captures,
  loading,
  restartLoading,
  errorMessage,
  statusText,
  titleText,
  isRegisterMode,
  ringStyle
} = toRefs(dataInfo);
</script>

<style scoped lang="scss">
.face-auth-page {
  --capture-center-y: 42%;
  --capture-size: 252px;
  --capture-radius: calc(var(--capture-size) / 2);
  --capture-hole-radius: 116px;
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: #fff;
}

.camera-video {
  position: fixed;
  top: calc(var(--capture-center-y) - var(--capture-radius));
  left: calc(50% - var(--capture-radius));
  width: var(--capture-size);
  height: var(--capture-size);
  border-radius: 50%;
  object-fit: cover;
  object-position: center center;
  background: #fff;
  transform: scaleX(-1);
}

.white-mask {
  position: fixed;
  inset: 0;
  pointer-events: none;
  background: radial-gradient(
    circle at center var(--capture-center-y),
    transparent 0 var(--capture-hole-radius),
    #fff calc(var(--capture-hole-radius) + 2px)
  );
}

.auth-stage {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  color: #1f2937;
}

.guide {
  padding: 44px 28px 0;
  text-align: center;
}

.guide h1 {
  margin: 0;
  font-size: 23px;
  font-weight: 700;
}

.guide p {
  min-height: 24px;
  margin: 10px 0 0;
  color: #697386;
  font-size: 14px;
}

.guide .register-tip {
  min-height: auto;
  margin-top: 8px;
  color: #d97706;
  font-size: 13px;
  line-height: 1.5;
}

.capture-ring {
  position: absolute;
  top: calc(var(--capture-center-y) - var(--capture-radius));
  left: calc(50% - var(--capture-radius));
  width: var(--capture-size);
  height: var(--capture-size);
  border-radius: 50%;
}

.capture-ring::before {
  position: absolute;
  inset: 0;
  content: "";
  border-radius: 50%;
  padding: 8px;
  background: conic-gradient(
    #1f6feb var(--capture-progress, 0deg),
    rgba(31, 111, 235, 0.16) var(--capture-progress, 0deg) 360deg
  );
  mask:
    linear-gradient(#000 0 0) content-box,
    linear-gradient(#000 0 0);
  mask-composite: exclude;
  -webkit-mask:
    linear-gradient(#000 0 0) content-box,
    linear-gradient(#000 0 0);
  -webkit-mask-composite: xor;
}

.capture-ring::after {
  position: absolute;
  inset: 8px;
  content: "";
  border: 3px solid #fff;
  border-radius: 50%;
  box-shadow:
    0 0 0 1px rgba(31, 111, 235, 0.18),
    0 10px 34px rgba(31, 111, 235, 0.16);
}

.ring-glow {
  position: absolute;
  inset: 18px;
  border-radius: 50%;
  border: 1px solid rgba(31, 111, 235, 0.28);
  animation: pulse 1.8s ease-in-out infinite;
}

.loading-state {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.scanner-line {
  position: absolute;
  top: 48px;
  left: 37px;
  width: 178px;
  height: 2px;
  border-radius: 999px;
  background: linear-gradient(90deg, transparent, rgba(31, 111, 235, 0.82), transparent);
  box-shadow: 0 0 18px rgba(31, 111, 235, 0.42);
  animation: scan 2s ease-in-out infinite;
}

.capture-count {
  position: absolute;
  top: calc(var(--capture-center-y) + var(--capture-radius) + 20px);
  left: 0;
  display: flex;
  width: 100%;
  justify-content: center;
  gap: 10px;
}

.capture-count span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(31, 111, 235, 0.18);
}

.capture-count span.active {
  width: 22px;
  border-radius: 999px;
  background: #1f6feb;
}

.action-area {
  position: absolute;
  right: 24px;
  bottom: 38px;
  left: 24px;
}

.capture-canvas {
  display: none;
}

@keyframes scan {
  0%,
  100% {
    transform: translateY(0);
    opacity: 0.35;
  }

  50% {
    transform: translateY(154px);
    opacity: 1;
  }
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(0.96);
    opacity: 0.42;
  }

  50% {
    transform: scale(1.04);
    opacity: 0.9;
  }
}
</style>
