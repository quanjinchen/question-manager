<template>
  <AppStateResult
    :status="resultStatus"
    :title="titleText"
    :message="descriptionText"
  >
    <van-button
      v-if="isFail"
      round
      block
      type="primary"
      class="retry-button"
      @click="retryAuth"
    >
      重新认证
    </van-button>
    <van-button
      v-else
      round
      block
      type="primary"
      class="retry-button"
      @click="closePage"
    >
      关闭页面
    </van-button>
  </AppStateResult>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import AppStateResult from '@/components/AppStateResult.vue';
import { getCurrentAuthAccount } from '@/apis/auth-session';

type WeixinBridge = {
  call?: (name: string) => void;
  invoke?: (name: string, params?: Record<string, never>, callback?: () => void) => void;
};

declare global {
  interface Window {
    WeixinJSBridge?: WeixinBridge;
  }
}

const route = useRoute();
const router = useRouter();
let closeTimer = 0;
let countdownTimer = 0;
const countdown = ref(3);

const resultStatus = computed(() => {
  const status = route.query.status;
  const value = Array.isArray(status) ? status[0] : status;
  return value === 'fail' ? 'fail' : 'success';
});

const isFail = computed(() => resultStatus.value === 'fail');
const titleText = computed(() => (isFail.value ? '认证失败' : '认证成功'));
const descriptionText = computed(() => (
  isFail.value ? '身份认证未通过，请重新认证' : `页面将在 ${countdown.value} 秒后自动关闭`
));

function getCertToken() {
  const token = route.query.certToken;
  return Array.isArray(token) ? token[0] || '' : token || '';
}

async function retryAuth() {
  const account = getCurrentAuthAccount(getCertToken());
  await router.replace({
    path: '/login',
    query: {
      certToken: account?.certToken || getCertToken()
    }
  });
}

function closeByWechatBridge() {
  if (!window.WeixinJSBridge) {
    return false;
  }

  window.WeixinJSBridge.invoke?.('closeWindow', {}, () => undefined);
  window.WeixinJSBridge.call?.('closeWindow');
  return true;
}

function closePage() {
  if (closeByWechatBridge()) {
    return;
  }

  document.addEventListener('WeixinJSBridgeReady', closeByWechatBridge, { once: true });
  window.close();
}

onMounted(() => {
  if (isFail.value) {
    return;
  }

  countdownTimer = window.setInterval(() => {
    countdown.value = Math.max(countdown.value - 1, 0);
  }, 1000);

  closeTimer = window.setTimeout(closePage, 3000);
});

onBeforeUnmount(() => {
  window.clearInterval(countdownTimer);
  window.clearTimeout(closeTimer);
});
</script>

<style scoped lang="scss">
.retry-button {
  width: 220px;
  margin: 0 auto;
}
</style>
