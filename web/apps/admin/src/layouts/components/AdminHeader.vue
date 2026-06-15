<template>
  <el-header class="AdminHeader-root">
    <div class="left"></div>
    <div class="right">
      <span class="welcome">你好，{{ adminStore.userInfo.fullName || "未登录用户" }}</span>
      <AppButton @click="logout">退出登录</AppButton>
    </div>
  </el-header>
</template>

<script setup lang="ts" name="AdminHeader">
import $utils from '@vue-scaffold/utils';
import { useRouter } from "vue-router";
import { AppButton } from "@vue-scaffold/pc-ui";
import { $apis } from "@/api/requests";
import { useAdminStore } from "@/stores";

const router = useRouter();
const adminStore = useAdminStore();
const sidebarWidth = adminStore.sidebarWidth;

async function logout() {
  await $utils.Message.messageConfirm("确认退出当前登录状态吗？", "退出确认", {
    confirmButtonText: "退出",
    cancelButtonText: "取消",
  });
  try {
    await $apis.login.logout({});
  } finally {
    adminStore.clearSession();
    await router.replace("/login");
  }
}
</script>

<style scoped lang="scss">
.AdminHeader-root {
  width: calc(100% - v-bind(sidebarWidth));
  margin-left: v-bind(sidebarWidth);
  height: 64px;
  background: rgba(255, 255, 255, 0.78);
  border-bottom: 1px solid rgba(148, 163, 184, 0.18);
  padding: 0 28px 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  backdrop-filter: blur(18px);
  box-shadow: 0 10px 32px rgba(15, 23, 42, 0.04);
  position: relative;
  z-index: 2;
}

.AdminHeader-root::after {
  content: "";
  position: absolute;
  left: 24px;
  right: 24px;
  bottom: 0;
  height: 1px;
  background: linear-gradient(90deg, rgba(0, 65, 192, 0.12), rgba(148, 163, 184, 0.08), rgba(94, 234, 212, 0.18));
}

.right {
  display: inline-flex;
  align-items: center;
  gap: 14px;
}

.right :deep(.AppButton-root.el-button) {
  height: 40px;
  padding: 0 18px;
  border-radius: 999px;
  border-color: rgba(15, 23, 42, 0.08);
  background: rgba(255, 255, 255, 0.86);
  color: #0f172a;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.06);
}

.right :deep(.AppButton-root.el-button:hover) {
  border-color: rgba(0, 65, 192, 0.18);
  background: #fff;
  color: #0041c0;
}

.welcome {
  display: inline-flex;
  align-items: center;
  min-height: 40px;
  padding: 0 14px;
  border-radius: 999px;
  color: #334155;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(148, 163, 184, 0.16);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.65);
  font-weight: 500;
}

.left {
  flex: 1;
  min-width: 1px;
}

@media (max-width: 960px) {
  .AdminHeader-root {
    width: calc(100% - v-bind(sidebarWidth));
    padding-right: 20px;
    padding-left: 20px;
  }

  .welcome {
    max-width: 220px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
