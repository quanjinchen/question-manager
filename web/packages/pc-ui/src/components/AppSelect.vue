<template>
  <el-select v-model="model" class="AppSelect-root" v-bind="selectPropsResult">
    <el-option v-for="item in list" :key="item.value" :label="item.label" :value="item.value" />
  </el-select>
</template>

<script setup lang="ts" name="AppSelect">
  import { computed, useAttrs } from 'vue';
  import type { PropType } from 'vue';
  import { useVModel } from '@vue-scaffold/hooks';
  import $utils from '@vue-scaffold/utils';

  defineOptions({ inheritAttrs: false });

  type SelectOption = {
    label: string;
    value: string | number;
  };

  const props = defineProps({
    modelValue: {
      type: [String, Number, Array],
      default: ''
    },
    list: {
      type: Array as PropType<SelectOption[]>,
      default: () => []
    },
    selectProps: {
      type: Object,
      default: () => ({})
    }
  });

  const emit = defineEmits(['update:modelValue']);

  const attrs = useAttrs();
  const model = useVModel(props, emit);
  const selectPropsResult = computed(() =>
    $utils.Object.deepAssign(
      {
        style: 'width: 100%',
        clearable: true
      },
      attrs as Record<string, any>,
      props.selectProps
    )
  );
</script>
