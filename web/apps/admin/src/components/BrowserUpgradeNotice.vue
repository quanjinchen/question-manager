<template>
  <AppDialog v-model="visible" :modal-props="{ title: 'Browser notice', width: 520 }" :footer-props="{ buttons: [] }">
    <el-result icon="warning" title="Modern browser recommended" sub-title="Please use a modern Chromium, Edge, Firefox, or Safari version for the best experience." />
  </AppDialog>
</template>

<script setup lang="ts" name="BrowserUpgradeNotice">
  import { onMounted, ref } from 'vue';

  const visible = ref(false);

  function isBrowserCompatible() {
    return typeof BigInt !== 'undefined' && typeof Object.fromEntries !== 'undefined';
  }

  onMounted(() => {
    visible.value = !isBrowserCompatible();
  });
</script>

