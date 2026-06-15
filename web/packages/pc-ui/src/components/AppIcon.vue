<template>
  <el-icon class="AppIcon-root">
    <component :is="resolvedIcon" />
  </el-icon>
</template>

<script setup lang="ts" name="AppIcon">
  import * as ElementPlusIcons from '@element-plus/icons-vue';
  import { computed } from 'vue';

  const props = defineProps({
    name: {
      type: String,
      default: ''
    }
  });

  const resolvedIcon = computed(() => {
    return (ElementPlusIcons as Record<string, unknown>)[props.name] ?? ElementPlusIcons.Menu;
  });
</script>
