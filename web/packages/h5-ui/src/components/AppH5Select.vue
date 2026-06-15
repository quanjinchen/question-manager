<template>
  <VanField
    class="AppH5Select-root"
    :model-value="selectedLabel"
    v-bind="fieldPropsResult"
    readonly
    clickable
    @click="visible = true"
  />
  <VanPopup v-model:show="visible" v-bind="popupPropsResult">
    <VanPicker
      v-bind="pickerPropsResult"
      :columns="columns"
      @cancel="visible = false"
      @confirm="handleConfirm"
    />
  </VanPopup>
</template>

<script setup lang="ts" name="AppH5Select">
  import { computed, ref, useAttrs } from 'vue';
  import type { PropType } from 'vue';
  import { Field as VanField, Picker as VanPicker, Popup as VanPopup } from 'vant';
  import $utils from '@vue-scaffold/utils';
  import type { AppH5Option, AppH5OptionValue } from './types';

  defineOptions({ inheritAttrs: false });

  const props = defineProps({
    modelValue: {
      type: [String, Number],
      default: ''
    },
    list: {
      type: Array as PropType<AppH5Option[]>,
      default: () => []
    },
    placeholder: {
      type: String,
      default: '请选择'
    },
    fieldProps: {
      type: Object,
      default: () => ({})
    },
    popupProps: {
      type: Object,
      default: () => ({})
    },
    pickerProps: {
      type: Object,
      default: () => ({})
    }
  });

  const emit = defineEmits(['update:modelValue', 'change']);
  const attrs = useAttrs();
  const visible = ref(false);
  const columns = computed(() =>
    props.list.map(item => ({
      text: item.label,
      value: item.value,
      disabled: item.disabled
    }))
  );
  const selectedLabel = computed(() =>
    props.list.find(item => item.value === props.modelValue)?.label || ''
  );
  const fieldPropsResult = computed(() =>
    $utils.Object.deepAssign(
      {
        placeholder: props.placeholder,
        isLink: true
      },
      attrs as Record<string, any>,
      props.fieldProps
    )
  );
  const popupPropsResult = computed(() =>
    $utils.Object.deepAssign(
      {
        round: true,
        position: 'bottom'
      },
      props.popupProps
    )
  );
  const pickerPropsResult = computed(() =>
    $utils.Object.deepAssign(
      {
        title: props.placeholder,
        columnsFieldNames: {
          text: 'text',
          value: 'value'
        }
      },
      props.pickerProps
    )
  );

  function handleConfirm(event: { selectedOptions?: Array<{ text: string; value: AppH5OptionValue }> }) {
    const selected = event.selectedOptions?.[0];
    if (!selected) {
      visible.value = false;
      return;
    }
    emit('update:modelValue', selected.value);
    emit('change', selected);
    visible.value = false;
  }
</script>
