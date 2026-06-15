<template>
  <el-breadcrumb
    class="AppBreadcrumb-root"
    :class="{ single: breadcrumbItems.length <= 1 }"
    separator="/"
  >
    <el-breadcrumb-item v-for="item in breadcrumbItems" :key="item.path">
      {{ item.title }}
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup lang="ts" name="AppBreadcrumb">
import { computed } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();

const breadcrumbItems = computed(() =>
  route.matched
    .filter((item) => item.meta?.title && item.name !== "root")
    .map((item) => ({
      path: item.path,
      title: String(item.meta?.title ?? item.name ?? item.path),
    })),
);
</script>

<style scoped lang="scss">
.AppBreadcrumb-root {
  min-height: 0;
  padding: 2px 4px 14px;
  background: transparent;
  border: 0;
  border-radius: 0;
  box-shadow: none;
  display: flex;
  align-items: center;
  flex: none;
  overflow-x: auto;
  overflow-y: hidden;
  font-size: 13px;
  line-height: 1;
  white-space: nowrap;
  scrollbar-width: none;
}

.AppBreadcrumb-root::-webkit-scrollbar {
  display: none;
}

.AppBreadcrumb-root.single :deep(.el-breadcrumb__item) {
  .el-breadcrumb__inner {
    color: #0f172a;
    font-weight: 700;
    letter-spacing: 0.01em;
  }
}

.AppBreadcrumb-root :deep(.el-breadcrumb__item) {
  display: inline-flex;
  align-items: center;

  .el-breadcrumb__separator {
    margin: 0 8px;
    color: #b2bccb;
    font-weight: 500;
  }

  .el-breadcrumb__inner {
    color: #94a3b8;
    font-weight: 500;
    transition: color 0.2s ease;

    &.is-link,
    a {
      font-weight: 500 !important;
      transition: color 0.2s ease;

      &:hover {
        color: #0041c0;
      }
    }
  }

  &:last-of-type {
    .el-breadcrumb__inner {
      color: #0f172a;
      font-weight: 700;
    }
  }
}
</style>
