<template>
  <div class="AppListHeader-root">
    <div class="header-search">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts" name="AppListHeader"></script>

<style scoped lang="scss">
  .AppListHeader-root {
    display: flex;
  align-items: flex-start;
    justify-content: space-between;
    gap: 16px;
  }

  .header-search {
    flex: 1;
    min-width: 0;
  }

  .AppListHeader-root :deep(.el-row) {
    row-gap: 8px;
  }
</style>
