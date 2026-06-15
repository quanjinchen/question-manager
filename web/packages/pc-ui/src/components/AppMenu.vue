<template>
  <el-menu
    class="AppMenu-root"
    :default-active="route.path"
    :collapse="collapsed"
    :router="true"
    background-color="transparent"
    text-color="#d0d5dd"
    active-text-color="#ffffff"
  >
    <AppMenuItems :list="list" />
  </el-menu>
</template>

<script setup lang="ts" name="AppMenu">
  import type { PropType } from 'vue';
  import { useRoute } from 'vue-router';
  import type { AccessMenuItem } from './types';
  import AppMenuItems from './AppMenuItems.vue';

  defineProps({
    list: {
      type: Array as PropType<AccessMenuItem[]>,
      default: () => []
    },
    collapsed: {
      type: Boolean,
      default: false
    }
  });

  const route = useRoute();
</script>

<style scoped lang="scss">
  .AppMenu-root {
    border-right: 0;

    :deep(.el-menu-item),
    :deep(.el-sub-menu__title) {
      overflow: hidden;
    }

    :deep(.el-menu-item > span),
    :deep(.el-sub-menu__title > span) {
      transition:
        opacity 0.18s ease,
        transform 0.18s ease;
    }

    :deep(.el-menu--collapse .el-menu-item > span),
    :deep(.el-menu--collapse .el-sub-menu__title > span) {
      opacity: 0;
      transform: translateX(-4px);
    }
  }
</style>
