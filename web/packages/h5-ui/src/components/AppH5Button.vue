<template>
  <VanButton class="AppH5Button-root" v-bind="buttonPropsResult">
    <slot />
  </VanButton>
</template>

<script setup lang="ts" name="AppH5Button">
  import { computed, useAttrs } from 'vue';
  import { Button as VanButton } from 'vant';
  import $utils from '@vue-scaffold/utils';

  defineOptions({ inheritAttrs: false });

  const props = defineProps({
    buttonProps: {
      type: Object,
      default: () => ({})
    }
  });

  const attrs = useAttrs();
  const buttonPropsResult = computed(() =>
    $utils.Object.deepAssign({}, attrs as Record<string, any>, props.buttonProps)
  );
</script>
