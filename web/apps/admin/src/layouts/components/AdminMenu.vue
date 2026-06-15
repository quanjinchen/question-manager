<template>
  <el-aside
    width="200px"
    class="AdminMenu-root"
    :style="{ width: adminStore.sidebarWidth }"
  >
    <div class="AdminMenu-inner">
      <div
        class="AdminMenu-brand"
        :class="{ 'is-collapsed': adminStore.sidebarCollapsed }"
      >
        <img
          class="AdminMenu-brandIcon"
          src="/favicon.svg"
          :alt="brandTitle"
        />
        <div class="AdminMenu-brandText">
          <strong>{{ brandTitle }}</strong>
          <span>{{ brandSubtitle }}</span>
        </div>
      </div>
      <AppMenu
        :list="adminStore.menuTree"
        :collapsed="adminStore.sidebarCollapsed"
        class="AdminMenu-menu"
      />
    </div>
  </el-aside>
</template>

<script setup lang="ts" name="AdminMenu">
import { computed } from "vue";
import { AppMenu } from "@vue-scaffold/pc-ui";
import { useAdminStore } from "@/stores";

const adminStore = useAdminStore();
const brandTitle = computed(() => adminStore.title);
const brandSubtitle = "Identity Console";
</script>

<style scoped lang="scss">
.AdminMenu-root {
  position: fixed;
  top: 0;
  left: 0;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.05), transparent 18%),
    linear-gradient(180deg, #111b2e 0%, #0b1220 100%);
  color: #fff;
  transition:
    width 0.2s ease,
    box-shadow 0.2s ease;
  overflow: visible;
  height: 100vh;
  border-right: 1px solid rgba(148, 163, 184, 0.16);
  box-shadow: inset -1px 0 0 rgba(255, 255, 255, 0.03);
  z-index: 5;
}

.AdminMenu-root::before {
  content: "";
  position: absolute;
  inset: 0 0 auto;
  height: 140px;
  background: radial-gradient(circle at top left, rgba(94, 234, 212, 0.18), transparent 58%);
  pointer-events: none;
}

.AdminMenu-inner {
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 18px 12px 20px;
  scrollbar-width: thin;
  scrollbar-color: rgba(148, 163, 184, 0.35) transparent;
}

.AdminMenu-inner::-webkit-scrollbar {
  width: 6px;
}

.AdminMenu-inner::-webkit-scrollbar-thumb {
  background: rgba(148, 163, 184, 0.35);
  border-radius: 999px;
}

.AdminMenu-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  min-height: 72px;
  margin-bottom: 16px;
  padding: 8px 6px 10px;
  overflow: hidden;
}

.AdminMenu-brandIcon {
  width: 36px;
  height: 36px;
  flex: none;
  border-radius: 10px;
}

.AdminMenu-brandText {
  display: flex;
  flex-direction: column;
  min-width: 0;
  transition:
    opacity 0.18s ease,
    transform 0.18s ease;
}

.AdminMenu-brandText strong {
  color: #f8fafc;
  font-size: 16px;
  font-weight: 700;
  line-height: 1.2;
  letter-spacing: 0.04em;
}

.AdminMenu-brandText span {
  margin-top: 5px;
  color: rgba(203, 213, 225, 0.64);
  font-size: 11px;
  line-height: 1;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.AdminMenu-brand.is-collapsed {
  justify-content: center;
  padding-right: 0;
  padding-left: 0;
}

.AdminMenu-brand.is-collapsed .AdminMenu-brandText {
  width: 0;
  opacity: 0;
  transform: translateX(-6px);
}

.AdminMenu-menu {
  border-right: 0;
  background: transparent;
}

.AdminMenu-menu :deep(.el-menu) {
  border-right: 0;
  background: transparent;
}

.AdminMenu-menu :deep(.el-menu-item),
.AdminMenu-menu :deep(.el-sub-menu__title) {
  height: 46px;
  margin-bottom: 6px;
  border-radius: 14px;
  color: #cbd5e1 !important;
  font-weight: 500;
  letter-spacing: 0.01em;
  transition:
    background 0.2s ease,
    color 0.2s ease,
    transform 0.2s ease;
}

.AdminMenu-menu :deep(.el-menu-item .el-icon),
.AdminMenu-menu :deep(.el-sub-menu__title .el-icon) {
  width: 18px;
  margin-right: 12px;
  font-size: 16px;
  color: rgba(226, 232, 240, 0.78);
  transition: color 0.2s ease;
}

.AdminMenu-menu :deep(.el-menu-item:hover),
.AdminMenu-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(148, 163, 184, 0.12) !important;
  color: #f8fafc !important;
  transform: translateX(2px);
}

.AdminMenu-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(0, 65, 192, 0.9), rgba(14, 165, 233, 0.78)) !important;
  color: #fff !important;
  box-shadow: 0 10px 24px rgba(0, 65, 192, 0.22);
}

.AdminMenu-menu :deep(.el-menu-item.is-active .el-icon),
.AdminMenu-menu :deep(.el-sub-menu.is-opened > .el-sub-menu__title .el-icon),
.AdminMenu-menu :deep(.el-menu-item:hover .el-icon),
.AdminMenu-menu :deep(.el-sub-menu__title:hover .el-icon) {
  color: #fff;
}

.AdminMenu-menu :deep(.el-sub-menu .el-menu-item) {
  min-width: 100%;
  padding-left: 44px !important;
  background: transparent !important;
}

.AdminMenu-menu :deep(.el-sub-menu .el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.12) !important;
  box-shadow: none;
}

.AdminMenu-menu :deep(.el-sub-menu .el-menu) {
  background: transparent !important;
}

.AdminMenu-menu :deep(.el-menu--collapse .el-menu-item),
.AdminMenu-menu :deep(.el-menu--collapse .el-sub-menu__title) {
  padding: 0 !important;
  justify-content: center;
}
</style>
