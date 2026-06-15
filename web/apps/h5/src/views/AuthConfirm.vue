<template>
  <main class="auth-confirm-page">
    <section class="app-panel">
      <div class="app-icon">
        <van-icon name="shield-o" />
      </div>
      <h1>{{ appName }}</h1>
      <p>正在请求使用当前身份信息完成人脸{{ actionText }}</p>
    </section>

    <van-cell-group
      inset
      title="授权信息"
    >
      <van-cell
        title="授权用户"
        :value="userName"
      />
      <van-cell
        title="证件号码"
        :value="maskedIdCard"
      />
    </van-cell-group>

    <section class="agreement-panel">
      <van-checkbox
        v-model="checked"
        shape="square"
      >
        我已阅读并同意
        <button
          type="button"
          class="text-button"
          @click.stop="dataInfo.openAgreement('user')"
        >
          用户协议
        </button>
        和
        <button
          type="button"
          class="text-button"
          @click.stop="dataInfo.openAgreement('privacy')"
        >
          隐私协议
        </button>
      </van-checkbox>
    </section>

    <van-button
      round
      block
      type="primary"
      class="confirm-button"
      :disabled="!checked"
      @click="dataInfo.confirm()"
    >
      同意并继续
    </van-button>

    <van-popup
      v-model:show="agreementVisible"
      round
      position="bottom"
      closeable
      class="agreement-popup"
    >
      <h2>{{ agreementTitle }}</h2>
      <div class="agreement-content">
        <p
          v-for="item in agreementContent"
          :key="item"
        >
          {{ item }}
        </p>
      </div>
    </van-popup>
  </main>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, toRefs } from 'vue';
import { showFailToast } from 'vant';
import { useRoute, useRouter } from 'vue-router';
import { getCurrentAuthAccount, type CachedAuthAccount } from '@/apis/auth-session';

const router = useRouter();
const route = useRoute();
const FACE_MODEL_URLS = [
  '/models/face-api/tiny_face_detector_model-weights_manifest.json',
  '/models/face-api/tiny_face_detector_model-shard1',
  '/models/face-api/face_landmark_68_tiny_model-weights_manifest.json',
  '/models/face-api/face_landmark_68_tiny_model-shard1'
];

const certToken = computed(() => {
  const token = route.query.certToken;
  return Array.isArray(token) ? token[0] || '' : token || '';
});

function preloadFaceModels() {
  FACE_MODEL_URLS.forEach(url => {
    fetch(url, { cache: 'force-cache' }).catch(() => {
      console.warn(`[face-api] 人脸模型预下载失败：${url}`);
    });
  });
}

const dataInfo = reactive({
  account: null as CachedAuthAccount | null,
  checked: false,
  agreementVisible: false,
  agreementType: 'user' as 'privacy' | 'user',
  get appName() {
    return this.account?.appInfo?.appName || '当前应用';
  },
  get clientId() {
    return this.account?.appInfo?.clientId || '-';
  },
  get userName() {
    return this.account?.fullName || '-';
  },
  get maskedIdCard() {
    const idCard = this.account?.idCard || '';
    if (idCard.length < 8) {
      return idCard || '-';
    }
    return `${idCard.slice(0, 4)}**********${idCard.slice(-4)}`;
  },
  get actionText() {
    return this.account?.registerMode ? '注册' : '认证';
  },
  get agreementTitle() {
    return this.agreementType === 'privacy' ? '隐私协议' : '用户协议';
  },
  get agreementContent() {
    if (this.agreementType === 'privacy') {
      return [
        '为完成身份核验，本页面将采集姓名、身份证号及人脸图片，并提交给当前登录应用及认证服务处理。',
        '人脸图片仅用于本次注册或认证比对，认证完成后按平台安全策略保存或清理。',
        '平台会采取必要的加密、访问控制和日志审计措施保护你的个人信息。'
      ];
    }
    return [
      '你确认当前操作由本人发起，并授权当前应用使用身份信息完成认证流程。',
      '请确保填写的姓名和身份证号真实有效，认证过程中请保持本人正对屏幕。',
      '如果你不同意相关条款，可以返回并停止本次认证。'
    ];
  },
  init() {
    if (!certToken.value) {
      showFailToast('certToken 不能为空');
      router.replace('/login');
      return;
    }

    const account = getCurrentAuthAccount(certToken.value);
    if (!account) {
      showFailToast('未找到认证账户信息');
      router.replace({
        path: '/login',
        query: { certToken: certToken.value }
      });
      return;
    }

    this.account = account;
  },
  openAgreement(type: 'privacy' | 'user') {
    this.agreementType = type;
    this.agreementVisible = true;
  },
  confirm() {
    if (!this.account) {
      showFailToast('未找到认证账户信息');
      return;
    }
    if (!this.checked) {
      showFailToast('请先同意协议');
      return;
    }

    router.replace({
      path: '/face-auth',
      query: {
        certToken: this.account.certToken,
        mode: this.account.registerMode ? 'register' : 'auth'
      }
    });
  }
});

onMounted(() => {
  dataInfo.init();
  preloadFaceModels();
});

const {
  checked,
  agreementVisible,
  appName,
  clientId,
  userName,
  maskedIdCard,
  actionText,
  agreementTitle,
  agreementContent
} = toRefs(dataInfo);
</script>

<style scoped lang="scss">
.auth-confirm-page {
  min-height: 100vh;
  padding-bottom: 28px;
  background: #f6f7fb;
}

.app-panel {
  padding: 34px 24px 24px;
  text-align: center;
}

.app-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  border-radius: 18px;
  background: #1f6feb;
  color: #fff;
  font-size: 34px;
}

h1 {
  margin: 18px 0 8px;
  color: #1f2937;
  font-size: 24px;
}

.app-panel p {
  margin: 0;
  color: #697386;
  font-size: 14px;
  line-height: 1.6;
}

.agreement-panel {
  margin: 18px 16px 0;
  padding: 14px 16px;
  border-radius: 8px;
  background: #fff;
  color: #1f2937;
  font-size: 13px;
  line-height: 1.6;
}

.text-button {
  padding: 0;
  border: 0;
  background: transparent;
  color: #1f6feb;
  font: inherit;
}

.confirm-button {
  width: calc(100% - 32px);
  margin: 24px auto 0;
}

.agreement-popup {
  max-height: 72vh;
  padding: 22px 20px 28px;
}

.agreement-popup h2 {
  margin: 0 0 14px;
  color: #1f2937;
  font-size: 18px;
}

.agreement-content {
  max-height: 52vh;
  overflow-y: auto;
  color: #697386;
  font-size: 14px;
  line-height: 1.7;
}

.agreement-content p {
  margin: 0 0 12px;
}
</style>
