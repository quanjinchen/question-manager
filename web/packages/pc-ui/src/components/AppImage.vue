<template>
  <el-image class="AppImage-root" v-bind="imagePropsResult">
    <template v-if="$slots.error" #error>
      <slot name="error" />
    </template>
    <template v-if="$slots.placeholder" #placeholder>
      <slot name="placeholder" />
    </template>
  </el-image>
</template>

<script setup lang="ts" name="AppImage">
  import { computed } from 'vue';
  import $utils from '@vue-scaffold/utils';

  const props = defineProps({
    src: {
      type: String,
      default: ''
    },
    imageProps: {
      type: Object,
      default: () => ({})
    }
  });

  const imagePropsResult = computed(() => $utils.Object.deepAssign({
    src: props.src,
    fit: 'cover'
  }, props.imageProps));
</script>

<style scoped lang="scss">
  .AppImage-root {
    width: 100%;
    height: 100%;
    display: block;
  }
</style>
