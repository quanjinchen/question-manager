<template>
  <el-select-v2 v-model="model" class="AppSelectV2-root" v-bind="selectPropsResult" :options="options" />
</template>

<script setup lang="ts" name="AppSelectV2">
  import { computed, useAttrs } from 'vue';
  import { useVModel } from '@vue-scaffold/hooks';
  import $utils from '@vue-scaffold/utils';

  defineOptions({ inheritAttrs: false });

  const props = defineProps({
    modelValue: {
      type: [String, Number, Array],
      default: ''
    },
    list: {
      type: Array,
      default: () => []
    },
    selectV2Props: {
      type: Object,
      default: () => ({})
    }
  });

  const emit = defineEmits(['update:modelValue']);

  const attrs = useAttrs();
  const model = useVModel(props, emit);
  const options = computed(() => props.list.map((item: any) => ({ label: item.name, value: item.id, ...item })));
  const selectPropsResult = computed(() =>
    $utils.Object.deepAssign(
      {
        style: 'width: 100%',
        clearable: true
      },
      attrs as Record<string, any>,
      props.selectV2Props
    )
  );
</script>
