<template>
  <VanCheckbox v-model="model" class="AppH5Checkbox-root" v-bind="checkboxPropsResult">
    <slot />
  </VanCheckbox>
</template>

<script setup lang="ts" name="AppH5Checkbox">
  import { computed, useAttrs } from 'vue';
  import { Checkbox as VanCheckbox } from 'vant';
  import { useVModel } from '@vue-scaffold/hooks';
  import $utils from '@vue-scaffold/utils';

  defineOptions({ inheritAttrs: false });

  const props = defineProps({
    modelValue: {
      type: [Boolean, String, Number],
      default: false
    },
    checkboxProps: {
      type: Object,
      default: () => ({})
    }
  });

  const emit = defineEmits(['update:modelValue']);
  const attrs = useAttrs();
  const model = useVModel(props, emit);
  const checkboxPropsResult = computed(() =>
    $utils.Object.deepAssign({}, attrs as Record<string, any>, props.checkboxProps)
  );
</script>
