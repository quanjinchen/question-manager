<template>
  <VanLoading class="AppH5Loading-root" v-bind="loadingPropsResult">
    <slot />
  </VanLoading>
</template>

<script setup lang="ts" name="AppH5Loading">
  import { computed, useAttrs } from 'vue';
  import { Loading as VanLoading } from 'vant';
  import $utils from '@vue-scaffold/utils';

  defineOptions({ inheritAttrs: false });

  const props = defineProps({
    loadingProps: {
      type: Object,
      default: () => ({})
    }
  });

  const attrs = useAttrs();
  const loadingPropsResult = computed(() =>
    $utils.Object.deepAssign({}, attrs as Record<string, any>, props.loadingProps)
  );
</script>
