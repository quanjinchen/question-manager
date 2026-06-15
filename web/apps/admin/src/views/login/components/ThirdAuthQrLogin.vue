<template>
  <article class="ThirdAuthQrLogin-root">
    <div
      v-loading="loading"
      class="qr-box"
    >
      <canvas
        ref="canvasRef"
        class="qr-canvas"
      />
      <button
        v-if="expired || errorMessage"
        type="button"
        class="qr-mask"
        @click="dataInfo.refreshQr()"
      >
        <span>{{ expired ? "二维码已超时" : errorMessage }}</span>
        <em>点击刷新</em>
      </button>
      <div
        v-else-if="authPassed"
        class="qr-success-mask"
      >
        <el-icon>
          <CircleCheckFilled />
        </el-icon>
        <span>认证成功</span>
      </div>
    </div>

    <div class="qr-status">
      <span>{{ statusText }}</span>
      <strong v-if="!expired && countdown > 0">{{ countdown }}s</strong>
    </div>

    <div class="qr-actions">
      <AppButton
        :button-props="{ loading, disabled: loading || authPassed }"
        @click="dataInfo.refreshQr()"
      >
        刷新二维码
      </AppButton>
    </div>
  </article>
</template>

<script setup lang="ts" name="ThirdAuthQrLogin">
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, toRefs, watch } from "vue";
import { CircleCheckFilled } from "@element-plus/icons-vue";
import QRCode from "qrcode";
import $utils from "@vue-scaffold/utils";
import { $apis } from "@/api/requests";

const THIRD_AUTH_NOT_PASSED_CODE = 63005;
const QR_LOGIN_REFRESH_CODES = [8005, 8006];
const QR_EXPIRE_SECONDS = 300;
const POLL_INTERVAL = 3000;
const LOGIN_SUCCESS_DELAY = 2000;

const props = withDefaults(defineProps<{
  active?: boolean;
}>(), {
  active: true,
});

const emit = defineEmits<{
  loginSuccess: [Record<string, any>];
}>();

const canvasRef = ref<HTMLCanvasElement>();
let pollTimer = 0;
let countdownTimer = 0;
let loginSuccessTimer = 0;

function resolveErrorBody(error: any) {
  return error?.response?.data ?? error?.response?.data?.data ?? {};
}

const dataInfo = reactive({
  certToken: "",
  authUrl: "",
  loading: false,
  polling: false,
  expired: false,
  errorMessage: "",
  authPassed: false,
  countdown: QR_EXPIRE_SECONDS,
  get statusText() {
    if (this.authPassed) {
      return "认证成功，正在进入系统";
    }
    if (this.loading) {
      return "正在生成二维码";
    }
    if (this.errorMessage) {
      return this.errorMessage;
    }
    if (this.expired) {
      return "二维码已超时";
    }
    return "请使用移动端扫码认证";
  },
  clearTimers() {
    window.clearInterval(pollTimer);
    window.clearInterval(countdownTimer);
    window.clearTimeout(loginSuccessTimer);
    pollTimer = 0;
    countdownTimer = 0;
    loginSuccessTimer = 0;
  },
  startCountdown() {
    window.clearInterval(countdownTimer);
    countdownTimer = window.setInterval(() => {
      this.countdown = Math.max(this.countdown - 1, 0);
      if (this.countdown <= 0) {
        this.expired = true;
        window.clearInterval(pollTimer);
      }
    }, 1000);
  },
  startPolling() {
    window.clearInterval(pollTimer);
    pollTimer = window.setInterval(() => {
      this.pollLoginResult();
    }, POLL_INTERVAL);
  },
  async renderQrCode() {
    await nextTick();
    if (!canvasRef.value || !this.authUrl) {
      return;
    }
    await QRCode.toCanvas(canvasRef.value, this.authUrl, {
      width: 220,
      margin: 1,
      color: {
        dark: "#111827",
        light: "#ffffff",
      },
    });
  },
  async refreshQr() {
    if (this.loading) {
      return;
    }

    this.clearTimers();
    this.loading = true;
    this.errorMessage = "";
    this.authPassed = false;
    this.expired = false;
    this.countdown = QR_EXPIRE_SECONDS;
    try {
      const data = await $apis.login.getThirdAuthCertToken({});
      this.certToken = String(data?.certToken || "");
      this.authUrl = String(data?.authUrl || "");
      await this.renderQrCode();
      this.startCountdown();
      this.startPolling();
    } catch (error: any) {
      const errorBody = resolveErrorBody(error);
      this.errorMessage = errorBody?.msg || errorBody?.message || "二维码生成失败";
    } finally {
      this.loading = false;
    }
  },
  async pollLoginResult() {
    if (!this.certToken || this.expired || this.polling) {
      return;
    }

    this.polling = true;
    try {
      const loginResult = await $apis.login.loginByCertToken({
        certToken: this.certToken,
      }, {
        alertError: false,
      });
      if (loginResult?.token) {
        this.clearTimers();
        this.authPassed = true;
        emit("loginSuccess", loginResult);
      }
    } catch (error: any) {
      const errorBody = resolveErrorBody(error);
      const code = Number(errorBody?.code ?? errorBody?.retCode);
      const message = errorBody?.msg || errorBody?.message || "扫码登录失败";
      if (QR_LOGIN_REFRESH_CODES.includes(code)) {
        this.clearTimers();
        $utils.Message.messageAlert({
          type: "error",
          message,
        });
        await this.refreshQr();
        return;
      }
      if (code && code !== THIRD_AUTH_NOT_PASSED_CODE) {
        this.clearTimers();
        this.errorMessage = message;
      }
    } finally {
      this.polling = false;
    }
  },
  init() {
    this.refreshQr();
  },
  dispose() {
    this.clearTimers();
    this.polling = false;
  },
});

watch(() => props.active, (active) => {
  if (active) {
    dataInfo.refreshQr();
    return;
  }
  dataInfo.dispose();
});

onMounted(() => {
  if (props.active) {
    dataInfo.init();
  }
});

onBeforeUnmount(() => {
  dataInfo.dispose();
});

const {
  authUrl,
  authPassed,
  countdown,
  errorMessage,
  expired,
  loading,
  statusText,
} = toRefs(dataInfo);

defineExpose({ dataInfo });
</script>

<style scoped lang="scss">
.ThirdAuthQrLogin-root {
  width: 100%;
  margin-top: 10px;
}

.qr-box {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 252px;
  height: 252px;
  margin: 0 auto;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #ffffff;
}

.qr-canvas {
  width: 220px;
  height: 220px;
}

.qr-mask {
  position: absolute;
  inset: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: 0;
  border-radius: 6px;
  background: rgba(17, 24, 39, 0.82);
  color: #ffffff;
  cursor: pointer;
}

.qr-mask span {
  max-width: 180px;
  font-size: 16px;
  font-weight: 700;
  line-height: 1.4;
}

.qr-mask em {
  font-style: normal;
  font-size: 13px;
}

.qr-success-mask {
  position: absolute;
  inset: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.9);
  color: #16a34a;
  text-align: center;
  box-shadow: inset 0 0 0 1px rgba(22, 163, 74, 0.14);
}

.qr-success-mask .el-icon {
  font-size: 46px;
}

.qr-success-mask span {
  color: #166534;
  font-size: 18px;
  font-weight: 700;
}

.qr-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 22px;
  margin-top: 16px;
  color: #4b5563;
  font-size: 14px;
}

.qr-status strong {
  color: #1f6feb;
  font-weight: 700;
}

.qr-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
}

.qr-actions :deep(.AppButton-root) {
  min-width: 120px;
  height: 40px;
}

.qr-actions a {
  color: #1f6feb;
  font-size: 14px;
  text-decoration: none;
}
</style>
