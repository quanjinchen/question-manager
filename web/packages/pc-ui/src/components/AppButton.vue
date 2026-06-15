<template>
  <el-button class="AppButton-root" v-bind="buttonPropsResult">
    <AppIcon v-if="iconName" :name="iconName" class="AppButton-icon" />
    <slot />
  </el-button>
</template>

<script setup lang="ts" name="AppButton">
  import { computed } from 'vue';
  import $utils from '@vue-scaffold/utils';
  import AppIcon from './AppIcon.vue';

  const props = defineProps({
    buttonProps: {
      type: Object,
      default: () => ({})
    },
    iconProps: {
      type: Object,
      default: () => ({})
    }
  });

  const buttonPropsResult = computed(() => $utils.Object.deepAssign({}, props.buttonProps));
  const iconName = computed(() => String(props.iconProps?.name ?? ''));
</script>

<style scoped lang="scss">
  .AppButton-root {
    :deep(.el-icon) {
      margin-right: 4px;
    }
  }

  .AppButton-icon {
    font-size: 14px;
  }
</style>
