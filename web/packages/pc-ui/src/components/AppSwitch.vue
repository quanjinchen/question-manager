<template>
  <el-switch v-model="model" class="AppSwitch-root" v-bind="switchPropsResult" />
</template>

<script setup lang="ts" name="AppSwitch">
  import { computed } from 'vue';
  import { useVModel } from '@vue-scaffold/hooks';
  import $utils from '@vue-scaffold/utils';

  const props = defineProps({
    modelValue: {
      type: Boolean,
      default: false
    },
    switchProps: {
      type: Object,
      default: () => ({})
    }
  });

  const emit = defineEmits(['update:modelValue']);

  const model = useVModel(props, emit);
  const switchPropsResult = computed(() => $utils.Object.deepAssign({}, props.switchProps));
</script>
