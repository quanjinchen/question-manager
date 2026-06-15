<template>
  <div class="AdminLayout-root">
    <el-container class="layout-wrapper">
      <AdminHeader />
      <el-container class="layout-container">
        <AdminMenu />
        <el-main class="AdminLayout-main-wrapper"
          ><main class="AdminLayout-main">
            <section class="AdminLayout-content">
              <AppBreadcrumb />
              <router-view />
            </section></main
        ></el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts" name="AdminLayout">
import { useAdminStore } from "@/stores";
import AdminHeader from "./components/AdminHeader.vue";
import AdminMenu from "./components/AdminMenu.vue";

const adminStore = useAdminStore();
</script>

<style scoped lang="scss">
.AdminLayout-root {
  --layout-bg: #f3f6fb;
  --sidebar-width: v-bind('adminStore.sidebarWidth');
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background:
    radial-gradient(circle at top left, rgba(0, 65, 192, 0.12), transparent 28%),
    radial-gradient(circle at top right, rgba(94, 234, 212, 0.08), transparent 24%),
    linear-gradient(180deg, #f7f9fc 0%, var(--layout-bg) 100%);

  .layout-wrapper {
    height: 100%;
    display: flex;
    flex-direction: column;
    min-height: 0;
  }

  .animate-enter-from {
    opacity: 0;
  }
  .animate-enter-to {
    opacity: 1;
  }
  .animate-leave-from {
    opacity: 1;
  }
  .animate-leave-to {
    opacity: 0;
  }
  .animate-enter-active,
  .animate-leave-active {
    transition: all 0.25s;
  }

  .layout-container {
    flex: 1;
    min-height: 0;
    padding-left: var(--sidebar-width);
    overflow: hidden;
  }
}

.AdminLayout-main-wrapper {
  flex: 1;
  min-width: 0;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 0;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.42), rgba(255, 255, 255, 0)),
    transparent;
}

.AdminLayout-main {
  min-width: 0;
  width: 100%;
  height: 100%;
}

.AdminLayout-content {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 0;
  height: 100%;
  overflow: hidden;
}

.AdminLayout-content :deep(.AppBreadcrumb-root) {
  margin-bottom: 0;
}

@media (max-width: 960px) {
  .AdminLayout-content {
    padding: 18px;
  }
}
</style>
