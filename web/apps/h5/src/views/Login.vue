<template>
  <main class="login-page">
    <div
      v-if="tokenChecking"
      class="page-loading"
    >
      <van-loading
        color="#1f6feb"
        size="28px"
      />
    </div>

    <AppStateResult
      v-else-if="tokenInvalidMessage"
      status="fail"
      title="认证链接已失效"
    />

    <template v-else>
      <section class="login-panel">
        <h1>身份信息核验</h1>
        <p>{{ panelDescription }}</p>

        <van-form
          ref="formRef"
          @submit="dataInfo.handleSubmit()"
        >
          <van-cell-group inset>
            <van-field
              v-model="form.fullName"
              name="fullName"
              label="姓名"
              placeholder="请输入姓名"
              :rules="[{ required: true, message: '请输入姓名' }]"
            />
            <van-field
              v-model="form.idCard"
              name="idCard"
              label="身份证号"
              placeholder="请输入身份证号"
              readonly
              clickable
              :rules="idCardRules"
              @click="dataInfo.showIdCardKeyboard()"
              @focus="dataInfo.showIdCardKeyboard()"
            />
          </van-cell-group>

          <van-number-keyboard
            v-model="form.idCard"
            :show="idCardKeyboardVisible"
            theme="custom"
            extra-key="X"
            close-button-text="完成"
            :maxlength="18"
            @blur="dataInfo.hideIdCardKeyboard()"
          />

          <van-button
            round
            block
            type="primary"
            native-type="submit"
            :loading="loading"
            class="submit-button"
          >
            开始认证
          </van-button>
        </van-form>
      </section>
    </template>
  </main>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, toRefs } from 'vue';
import { showDialog, showFailToast } from 'vant';
import { useRoute, useRouter } from 'vue-router';
import { $apis } from '@/apis/requests';
import AppStateResult from '@/components/AppStateResult.vue';
import {
  getCachedAuthIdentity,
  setCachedAuthIdentity,
  setCurrentAuthAccount,
  type CachedAuthAccount
} from '@/apis/auth-session';
import type { AppBusinessError } from '@/apis/app-request';

const router = useRouter();
const route = useRoute();
const formRef = ref<{ validate: (name?: string | string[]) => Promise<void> }>();
const ID_CARD_PATTERN =
  /^(?:[1-9]\d{5}(?:18|19|20)\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\d|3[01])\d{3}[\dXx]|[1-9]\d{5}\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\d|3[01])\d{3})$/;

const certToken = computed(() => {
  const token = route.query.certToken;
  return Array.isArray(token) ? token[0] || '' : token || '';
});

const idCardRules = [
  { required: true, message: '请输入身份证号' },
  {
    validator: (value: string) => ID_CARD_PATTERN.test(value),
    message: '身份证号格式不正确'
  }
];

const dataInfo = reactive({
  form: {
    fullName: '',
    idCard: ''
  },
  tokenChecking: true,
  tokenInvalidMessage: '',
  loading: false,
  idCardKeyboardVisible: false,
  get panelDescription() {
    return '请输入姓名和身份证号，继续完成人脸认证';
  },
  get params() {
    return {
      certToken: certToken.value,
      ...this.form,
      idCard: this.form.idCard.toUpperCase()
    };
  },
  async queryAccount(account: CachedAuthAccount, silentError = false) {
    try {
      const accountInfo = await $apis.auth.queryAccount({
        certToken: account.certToken,
        fullName: account.fullName,
        idCard: account.idCard
      });
      const faceRegistered = Boolean(accountInfo?.faceRegistered);
      const allowFaceSelfRegister = accountInfo?.appInfo?.allowFaceSelfRegister === true;
      if (!faceRegistered && !allowFaceSelfRegister) {
        await showDialog({
          title: '未注册人脸',
          message: '当前账号未注册人脸，请联系管理员完成注册后再认证。',
          confirmButtonText: '我知道了'
        });
        return false;
      }
      setCachedAuthIdentity({
        fullName: account.fullName,
        idCard: account.idCard
      });
      setCurrentAuthAccount({
        ...account,
        token: accountInfo?.token || account.token || '',
        faceRegistered,
        appInfo: accountInfo?.appInfo ?? account.appInfo ?? null,
        registerMode: !faceRegistered
      });
      await router.replace({
        path: '/auth-confirm',
        query: { certToken: account.certToken }
      });
      return true;
    } catch (error) {
      const businessError = error as AppBusinessError;
      if (!silentError) {
        showFailToast(businessError.message || '账户核验失败');
        throw error;
      }
      return false;
    }
  },
  async checkCertToken() {
    if (!certToken.value) {
      this.tokenInvalidMessage = 'certToken 不能为空';
      return false;
    }

    try {
      await $apis.auth.checkCertToken({ certToken: certToken.value });
      return true;
    } catch (error) {
      const businessError = error as AppBusinessError;
      this.tokenInvalidMessage = businessError.message || 'certToken 已失效';
      return false;
    }
  },
  async init() {
    this.tokenChecking = true;
    try {
      const tokenValid = await this.checkCertToken();
      if (!tokenValid) {
        return;
      }

      const cachedIdentity = getCachedAuthIdentity();
      if (!cachedIdentity) {
        return;
      }

      const cachedAccount = {
        certToken: certToken.value,
        fullName: cachedIdentity.fullName,
        idCard: cachedIdentity.idCard.toUpperCase()
      };
      const autoRedirected = await this.queryAccount(cachedAccount, true);
      if (autoRedirected) {
        return;
      }

      this.form.fullName = cachedAccount.fullName;
      this.form.idCard = cachedAccount.idCard.toUpperCase();
    } finally {
      this.tokenChecking = false;
    }
  },
  async handleSubmit() {
    if (this.loading) {
      return;
    }
    if (!certToken.value) {
      showFailToast('certToken 不能为空');
      return;
    }

    this.loading = true;
    try {
      await this.queryAccount(this.params);
    } finally {
      this.loading = false;
    }
  },
  showIdCardKeyboard() {
    this.idCardKeyboardVisible = true;
  },
  async hideIdCardKeyboard() {
    this.idCardKeyboardVisible = false;
    if (this.form.idCard) {
      try {
        await formRef.value?.validate('idCard');
      } catch {
        // 关闭自定义键盘时触发一次校验，错误展示交给 van-form 自身处理。
      }
    }
  }
});

onMounted(() => {
  dataInfo.init();
});

const {
  form,
  tokenChecking,
  tokenInvalidMessage,
  loading,
  idCardKeyboardVisible,
  panelDescription
} = toRefs(dataInfo);
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  background: #f6f7fb;
}

.page-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: #fff;
}

.login-panel {
  padding: 40px 0 24px;
}

h1,
p {
  padding: 0 24px;
}

h1 {
  margin: 0 0 10px;
  color: #1f2937;
  font-size: 28px;
}

p {
  margin: 0 0 28px;
  color: #697386;
  font-size: 14px;
  line-height: 1.6;
}

.submit-button {
  width: calc(100% - 32px);
  margin: 28px auto 0;
}

</style>
