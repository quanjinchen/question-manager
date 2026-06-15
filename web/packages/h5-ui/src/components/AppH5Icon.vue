<template>
  <VanIcon class="AppH5Icon-root" v-bind="iconPropsResult" />
</template>

<script setup lang="ts" name="AppH5Icon">
  import { computed, useAttrs } from 'vue';
  import { Icon as VanIcon } from 'vant';
  import $utils from '@vue-scaffold/utils';

  defineOptions({ inheritAttrs: false });

  const props = defineProps({
    iconProps: {
      type: Object,
      default: () => ({})
    }
  });

  const attrs = useAttrs();
  const iconPropsResult = computed(() =>
    $utils.Object.deepAssign({}, attrs as Record<string, any>, props.iconProps)
  );
</script>
