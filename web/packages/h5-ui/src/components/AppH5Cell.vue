<template>
  <VanCell class="AppH5Cell-root" v-bind="cellPropsResult">
    <template v-if="$slots.title" #title>
      <slot name="title" />
    </template>
    <template v-if="$slots.value" #value>
      <slot name="value" />
    </template>
    <template v-if="$slots.label" #label>
      <slot name="label" />
    </template>
    <template v-if="$slots.icon" #icon>
      <slot name="icon" />
    </template>
    <template v-if="$slots.rightIcon" #right-icon>
      <slot name="rightIcon" />
    </template>
    <slot />
  </VanCell>
</template>

<script setup lang="ts" name="AppH5Cell">
  import { computed, useAttrs } from 'vue';
  import { Cell as VanCell } from 'vant';
  import $utils from '@vue-scaffold/utils';

  defineOptions({ inheritAttrs: false });

  const props = defineProps({
    cellProps: {
      type: Object,
      default: () => ({})
    }
  });

  const attrs = useAttrs();
  const cellPropsResult = computed(() =>
    $utils.Object.deepAssign({}, attrs as Record<string, any>, props.cellProps)
  );
</script>
