<template>
  <main class="Login-root">
    <section class="login-panel">
      <div class="brand">
        <h1>题库答题系统</h1>
        <p>登录后查看已授权题库并开始答题</p>
      </div>
      <van-form @submit="dataInfo.handleLogin()">
        <van-cell-group inset>
          <van-field v-model="formData.account" name="account" label="账号" placeholder="请输入账号" :rules="[{ required: true, message: '请输入账号' }]" />
          <van-field v-model="formData.password" type="password" name="password" label="密码" placeholder="请输入密码" :rules="[{ required: true, message: '请输入密码' }]" />
        </van-cell-group>
        <div class="login-actions">
          <van-button block type="primary" native-type="submit" :loading="loading">登录</van-button>
        </div>
      </van-form>
    </section>
  </main>
</template>

<script setup lang="ts" name="Login">
import { reactive, toRefs } from 'vue';
import { useRouter } from 'vue-router';
import { showFailToast } from 'vant';
import { $apis } from '@/apis/requests';
import { setCurrentAuthAccount } from '@/apis/auth-session';

const router = useRouter();

const dataInfo: any = reactive({
  formData: {
    account: '',
    password: '',
  },
  loading: false,
  async handleLogin() {
    if (this.loading) return;
    this.loading = true;
    try {
      const data = await $apis.questionPortal.login(this.formData);
      setCurrentAuthAccount({
        userId: data.userId,
        username: data.username,
        fullName: data.fullName,
        token: data.token,
      });
      await router.replace('/categories');
    } catch (error: any) {
      showFailToast(error?.message || '登录失败');
    } finally {
      this.loading = false;
    }
  },
});

const { formData, loading } = toRefs(dataInfo);
</script>

<style scoped lang="scss">
.Login-root {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fb 0%, #edf6f4 55%, #f7f3ea 100%);
}

.login-panel {
  width: min(420px, 100%);
}

.brand {
  margin-bottom: 24px;
  text-align: center;
}

.brand h1 {
  margin: 0 0 8px;
  color: #1f2937;
  font-size: 28px;
  letter-spacing: 0;
}

.brand p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.login-actions {
  margin-top: 20px;
}
</style>
