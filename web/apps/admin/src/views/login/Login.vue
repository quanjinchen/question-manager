<template>
  <main class="Login-root">
    <div class="Login-left">
      <LoginHeroPanel />
    </div>

    <div class="Login-right">
      <section class="container">
        <div class="panel">
          <button
            type="button"
            class="login-mode-toggle"
            :title="dataInfo.modeToggleTitle"
            @click="dataInfo.toggleLoginMode()"
          >
            <el-icon>
              <component :is="dataInfo.currentModeConfig.toggleIcon" />
            </el-icon>
          </button>

          <div class="thead">
            <p class="eyebrow">AUTH PLATFORM</p>
            <h3>{{ dataInfo.loginTitle }}</h3>
          </div>

          <Account
            v-if="dataInfo.loginModeVisible.password"
            ref="accountRef"
            :btn-loading="dataInfo.btnLoading"
            @on-login="dataInfo.handleLogin"
          />
          <ThirdAuthQrLogin
            v-if="dataInfo.loginModeVisible.qr"
            ref="qrLoginRef"
            :active="dataInfo.loginModeVisible.qr"
            @login-success="dataInfo.handleLogin($event)"
          />
        </div>
      </section>
    </div>
  </main>
</template>

<script setup lang="ts" name="Login">
import { nextTick, onMounted, reactive, ref, type Component } from "vue";
import $utils from '@vue-scaffold/utils';
import { Grid, UserFilled } from "@element-plus/icons-vue";
import { useRoute, useRouter } from "vue-router";
import { $apis } from "@/api/requests";
import { getFirstMenuPath } from "@/router/menu";
import { useAdminStore } from "@/stores";
import Account from "@/views/login/components/Account.vue";
import LoginHeroPanel from "@/views/login/components/LoginHeroPanel.vue";
import ThirdAuthQrLogin from "@/views/login/components/ThirdAuthQrLogin.vue";

const DEFAULT_REDIRECT_PATH = "/";
type LoginMode = "password" | "qr";
type LoginPayload = {
  params?: Record<string, string>;
};
type LoginResult = Record<string, any>;
type LoginModeConfig = {
  mode: LoginMode;
  loginTitle: string;
  modeToggleTitle: string;
  toggleIcon: Component;
  nextMode: LoginMode;
};

const router = useRouter();
const route = useRoute();
const adminStore = useAdminStore();
const accountRef = ref<InstanceType<typeof Account>>();
const qrLoginRef = ref<InstanceType<typeof ThirdAuthQrLogin>>();

function initPasswordLogin() {
  nextTick(() => {
    accountRef.value?.dataInfo.init();
  });
}

function initQrLogin() {
  nextTick(() => {
    qrLoginRef.value?.dataInfo.refreshQr();
  });
}

function resolvePasswordLoginResult(payload: LoginPayload | LoginResult) {
  const loginPayload = payload as LoginPayload;
  return $apis.login.accountLogin(loginPayload.params || {});
}

function resolveQrLoginResult(payload: LoginPayload | LoginResult) {
  return payload as LoginResult;
}

const LOGIN_MODE_ENTER_MAP: Record<LoginMode, () => void> = {
  password: initPasswordLogin,
  qr: initQrLogin,
};

const LOGIN_RESULT_RESOLVER_MAP: Record<
  LoginMode,
  (payload: LoginPayload | LoginResult) => Promise<LoginResult> | LoginResult
> = {
  password: resolvePasswordLoginResult,
  qr: resolveQrLoginResult,
};

const LOGIN_MODE_OPTIONS: LoginModeConfig[] = [
  {
    mode: "password",
    loginTitle: "账号密码登录",
    modeToggleTitle: "切换扫码登录",
    toggleIcon: Grid,
    nextMode: "qr",
  },
  {
    mode: "qr",
    loginTitle: "扫码登录",
    modeToggleTitle: "切换密码登录",
    toggleIcon: UserFilled,
    nextMode: "password",
  },
];

function getLoginModeConfig(loginMode: LoginMode) {
  return LOGIN_MODE_OPTIONS.find((item) => item.mode === loginMode) || LOGIN_MODE_OPTIONS[0];
}

const dataInfo = reactive({
  loginMode: "password" as LoginMode,
  btnLoading: false,
  get currentModeConfig() {
    return getLoginModeConfig(this.loginMode as LoginMode);
  },
  get loginModeVisible() {
    return LOGIN_MODE_OPTIONS.reduce((visibleMap, item) => {
      visibleMap[item.mode] = item.mode === this.loginMode;
      return visibleMap;
    }, {} as Record<LoginMode, boolean>);
  },
  get loginTitle() {
    return this.currentModeConfig.loginTitle;
  },
  get modeToggleTitle() {
    return this.currentModeConfig.modeToggleTitle;
  },
  init() {
    accountRef.value?.dataInfo.init();
  },
  resetVerifyCode() {
    accountRef.value?.dataInfo.resetCodeValue();
  },
  toggleLoginMode() {
    this.loginMode = this.currentModeConfig.nextMode;
    LOGIN_MODE_ENTER_MAP[this.loginMode]();
  },
  async completeLogin(loginResult: Record<string, any>) {
    const token = String(loginResult?.token ?? "");
    adminStore.setToken(token);
    const userInfo = await $apis.login.getLoginInfo({});
    if (!userInfo.menus.length) {
      adminStore.clearSession();
      $utils.Message.messageAlert({ message: "没有授予权限",type: "error" });
      LOGIN_MODE_ENTER_MAP[this.loginMode]?.();
      throw new Error("no granted permission");
    }
    adminStore.initUserInfo(userInfo);
    const redirectPath = String(route.query.redirect ?? "");
    const defaultPath = getFirstMenuPath(adminStore.menuTree);
    $utils.Message.messageAlert({
      message: `欢迎使用${adminStore.title}！`,
    });
    setTimeout(() => {
      router.replace(redirectPath || defaultPath || DEFAULT_REDIRECT_PATH);
    },1500)
  },
  async resolveLoginResult(payload: LoginPayload | Record<string, any>) {
    return LOGIN_RESULT_RESOLVER_MAP[this.loginMode](payload);
  },
  async handleLogin(payload: LoginPayload | Record<string, any>) {
    dataInfo.btnLoading = true;
    try {
      const loginResult = await dataInfo.resolveLoginResult(payload);
      await dataInfo.completeLogin(loginResult);
    } catch {
      if (dataInfo.loginMode === "password") {
        dataInfo.resetVerifyCode();
      }
    } finally {
      dataInfo.btnLoading = false;
    }
  },
});

onMounted(() => {
  dataInfo.init();
});
</script>

<style scoped lang="scss">
.Login-root {
  min-height: 100vh;
  display: flex;
  overflow: hidden;

  @media (max-width: 768px) {
    flex-direction: column;
  }
}

.Login-left {
  flex: 0 0 60%;
  position: relative;

  @media (max-width: 768px) {
    min-height: 300px;
    flex: none;
  }
}

.Login-right {
  flex: 0 0 40%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: #ffffff;

  @media (max-width: 768px) {
    flex: 1;
    padding: 24px;
  }
}

.container {
  width: 100%;
  max-width: 440px;
}

.panel {
  position: relative;
  padding: 0;
  background: transparent;
  box-shadow: none;
}

.login-mode-toggle {
  position: absolute;
  top: 0;
  right: 0;
  display: inline-flex;
  align-items: flex-start;
  justify-content: flex-end;
  width: 58px;
  height: 58px;
  padding: 8px 8px 0 0;
  border: 0;
  border-radius: 0 8px 0 0;
  background: linear-gradient(45deg, transparent 0 50%, #1f6feb 50% 100%);
  color: #ffffff;
  cursor: pointer;
}

.login-mode-toggle .el-icon {
  font-size: 22px;
}

.thead {
  margin-bottom: 20px;
}

.eyebrow {
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #8b5cf6;
  margin: 0 0 8px;
}

h3 {
  margin: 0 0 12px;
  font-size: 30px;
  line-height: 1.1;
  color: #1a202c;

  @media (max-width: 768px) {
    font-size: 24px;
  }
}

.description {
  margin: 0;
  color: #556176;
}
</style>
