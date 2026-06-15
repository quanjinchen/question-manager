<template>
  <template v-for="item in list" :key="item.path">
    <el-sub-menu v-if="item.children?.length" :index="item.path">
      <template #title>
        <AppIcon v-if="item.icon" :name="item.icon" />
        <span>{{ item.menuName }}</span>
      </template>
      <AppMenuItems :list="item.children" />
    </el-sub-menu>

    <el-menu-item v-else :index="item.path">
      <AppIcon v-if="item.icon" :name="item.icon" />
      <span>{{ item.menuName }}</span>
    </el-menu-item>
  </template>
</template>

<script setup lang="ts" name="AppMenuItems">
  import type { PropType } from 'vue';
  import type { AccessMenuItem } from './types';
  import AppIcon from './AppIcon.vue';

  defineProps({
    list: {
      type: Array as PropType<AccessMenuItem[]>,
      default: () => []
    }
  });
</script>
