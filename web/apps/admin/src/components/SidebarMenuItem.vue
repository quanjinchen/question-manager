<template>
  <el-sub-menu v-if="item.children?.length" :index="item.path">
    <template #title>
      <AppIcon v-if="item.icon" :name="item.icon" />
      <span>{{ item.menuName }}</span>
    </template>
    <SidebarMenuItem v-for="child in item.children" :key="child.path" :item="child" />
  </el-sub-menu>

  <el-menu-item v-else :index="item.path">
    <AppIcon v-if="item.icon" :name="item.icon" />
    <span>{{ item.menuName }}</span>
  </el-menu-item>
</template>

<script setup lang="ts" name="SidebarMenuItem">
  import type { PropType } from 'vue';
  import type { AccessMenuItem } from '@/router/menu';

  defineProps({
    item: {
      type: Object as PropType<AccessMenuItem>,
      required: true
    }
  });
</script>
