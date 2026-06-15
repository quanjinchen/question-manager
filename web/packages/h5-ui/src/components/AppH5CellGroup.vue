<template>
  <VanCellGroup class="AppH5CellGroup-root" v-bind="cellGroupPropsResult">
    <slot />
  </VanCellGroup>
</template>

<script setup lang="ts" name="AppH5CellGroup">
  import { computed, useAttrs } from 'vue';
  import { CellGroup as VanCellGroup } from 'vant';
  import $utils from '@vue-scaffold/utils';

  defineOptions({ inheritAttrs: false });

  const props = defineProps({
    cellGroupProps: {
      type: Object,
      default: () => ({})
    }
  });

  const attrs = useAttrs();
  const cellGroupPropsResult = computed(() =>
    $utils.Object.deepAssign({}, attrs as Record<string, any>, props.cellGroupProps)
  );
</script>
