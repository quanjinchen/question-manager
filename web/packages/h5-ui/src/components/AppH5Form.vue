<template>
  <VanForm ref="formRef" class="AppH5Form-root" v-bind="formPropsResult">
    <slot />
  </VanForm>
</template>

<script setup lang="ts" name="AppH5Form">
  import { computed, ref, useAttrs } from 'vue';
  import { Form as VanForm } from 'vant';
  import $utils from '@vue-scaffold/utils';

  defineOptions({ inheritAttrs: false });

  const props = defineProps({
    formProps: {
      type: Object,
      default: () => ({})
    }
  });

  const attrs = useAttrs();
  const formRef = ref<InstanceType<typeof VanForm>>();
  const formPropsResult = computed(() =>
    $utils.Object.deepAssign({}, attrs as Record<string, any>, props.formProps)
  );

  defineExpose({
    validate: (...args: any[]) => formRef.value?.validate(...args),
    resetValidation: (...args: any[]) => formRef.value?.resetValidation(...args),
    scrollToField: (...args: any[]) => formRef.value?.scrollToField(...args),
    getValidationStatus: (...args: any[]) => formRef.value?.getValidationStatus(...args)
  });
</script>
