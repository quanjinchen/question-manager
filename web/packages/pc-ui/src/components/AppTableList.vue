<template>
  <div class="AppTableList-root surface-table-card">
    <slot />
  </div>
</template>

<script setup lang="ts" name="AppTableList"></script>

<style scoped lang="scss">
.AppTableList-root {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 0;
  height: 100%;
  overflow: hidden;
}
.surface-table-card {
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid #d8e0ec;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(15, 23, 42, 0.04);
  padding: 16px;
}
.AppTableList-root :deep(.AppTable-root) {
  flex: 1;
  min-height: 0;
  overflow: auto;
}
</style>
