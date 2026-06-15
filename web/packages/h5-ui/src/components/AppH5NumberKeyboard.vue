<template>
  <VanNumberKeyboard
    v-model="model"
    class="AppH5NumberKeyboard-root"
    v-bind="keyboardPropsResult"
  />
</template>

<script setup lang="ts" name="AppH5NumberKeyboard">
  import { computed, useAttrs } from 'vue';
  import { NumberKeyboard as VanNumberKeyboard } from 'vant';
  import { useVModel } from '@vue-scaffold/hooks';
  import $utils from '@vue-scaffold/utils';

  defineOptions({ inheritAttrs: false });

  const props = defineProps({
    modelValue: {
      type: String,
      default: ''
    },
    keyboardProps: {
      type: Object,
      default: () => ({})
    }
  });

  const emit = defineEmits(['update:modelValue']);
  const attrs = useAttrs();
  const model = useVModel(props, emit);
  const keyboardPropsResult = computed(() =>
    $utils.Object.deepAssign({}, attrs as Record<string, any>, props.keyboardProps)
  );
</script>
