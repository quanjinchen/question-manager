<template>
  <div class="GraphVerifyCode-root">
    <AppInput v-model="dataInfo.verifyCode" placeholder="请输入验证码" />
    <div class="code-img" @click="dataInfo.init()">
      <AppImage :src="dataInfo.detail.img" />
    </div>
  </div>
</template>

<script setup lang="ts" name="GraphVerifyCode">
import { reactive } from 'vue';
import $utils from '@vue-scaffold/utils';
import { $apis } from '@/api/requests';

type CaptchaDetail = {
  img?: string;
  uuid?: string;
};

const dataInfo = reactive({
  verifyCode: '',
  uuid: '',
  loading: false,
  detail: {} as CaptchaDetail,
  async init() {
    if (this.loading) {
      return;
    }

    this.loading = true;
    this.verifyCode = '';
    try {
      const data = await $apis.login.getCaptcha({}, {
        alertError: false,
        needLogin: false
      });
      this.detail = data || {};
      this.uuid = data?.uuid || '';
    } catch {
      this.detail = {};
      this.uuid = '';
      $utils.Message.messageAlert({
        type: 'error',
        message: '验证码获取失败，请稍后重试'
      });
    } finally {
      this.loading = false;
    }
  }
});

defineExpose({ dataInfo });
</script>

<style scoped lang="scss">
  .GraphVerifyCode-root {
    width: 100%;
    height: 44px;
    display: flex;
    gap: 16px;
  }

  .GraphVerifyCode-root :deep(.AppInput-root) {
    height: 44px;
  }

  .code-img {
    width: 112px;
    height: 44px;
    border-radius: var(--el-border-radius-base);
    overflow: hidden;
    flex: none;
    cursor: pointer;
    background: #f3f5f8;
  }
</style>
