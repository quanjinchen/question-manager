<template>
  <main class="Login-root">
    <div class="Login-left">
      <LoginHeroPanel />
    </div>

    <div class="Login-right">
      <section class="container">
        <div class="panel">
          <div class="thead">
            <p class="eyebrow">QUESTION MANAGER</p>
            <h3>账号密码登录</h3>
          </div>

          <Account
            ref="accountRef"
            :btn-loading="dataInfo.btnLoading"
            @on-login="dataInfo.handleLogin"
          />
        </div>
      </section>
    </div>
  </main>
</template>

<script setup lang="ts" name="Login">
import { onMounted, reactive, ref } from "vue";
import $utils from '@vue-scaffold/utils';
import { useRoute, useRouter } from "vue-router";
import { $apis } from "@/api/requests";
import { getFirstMenuPath } from "@/router/menu";
import { useAdminStore } from "@/stores";
import Account from "@/views/login/components/Account.vue";
import LoginHeroPanel from "@/views/login/components/LoginHeroPanel.vue";

const DEFAULT_REDIRECT_PATH = "/";
type LoginPayload = {
  params?: Record<string, string>;
};
type LoginResult = Record<string, any>;

const router = useRouter();
const route = useRoute();
const adminStore = useAdminStore();
const accountRef = ref<InstanceType<typeof Account>>();

function resolvePasswordLoginResult(payload: LoginPayload | LoginResult) {
  const loginPayload = payload as LoginPayload;
  return $apis.login.accountLogin(loginPayload.params || {});
}

const dataInfo = reactive({
  btnLoading: false,
  init() {
    accountRef.value?.dataInfo.init();
  },
  resetVerifyCode() {
    accountRef.value?.dataInfo.resetCodeValue();
  },
  async completeLogin(loginResult: Record<string, any>) {
    const token = String(loginResult?.token ?? "");
    adminStore.setToken(token);
    const userInfo = await $apis.login.getLoginInfo({});
    if (!userInfo.menus.length) {
      adminStore.clearSession();
      $utils.Message.messageAlert({ message: "没有授予权限",type: "error" });
      this.init();
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
    return resolvePasswordLoginResult(payload);
  },
  async handleLogin(payload: LoginPayload | Record<string, any>) {
    dataInfo.btnLoading = true;
    try {
      const loginResult = await dataInfo.resolveLoginResult(payload);
      await dataInfo.completeLogin(loginResult);
    } catch {
      dataInfo.resetVerifyCode();
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
