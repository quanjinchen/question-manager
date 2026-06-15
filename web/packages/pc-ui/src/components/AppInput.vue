<template>
  <el-input v-model="model" class="AppInput-root" v-bind="inputPropsResult">
    <template v-if="prefixIconName" #prefix>
      <AppIcon :name="prefixIconName" />
    </template>
    <template v-if="suffixIconName" #suffix>
      <AppIcon :name="suffixIconName" />
    </template>
  </el-input>
</template>

<script setup lang="ts" name="AppInput">
  import { computed } from 'vue';
  import { useVModel } from '@vue-scaffold/hooks';
  import $utils from '@vue-scaffold/utils';
  import AppIcon from './AppIcon.vue';

  const props = defineProps({
    modelValue: {
      type: [String, Number],
      default: ''
    },
    inputProps: {
      type: Object,
      default: () => ({})
    },
    iconProps: {
      type: Object,
      default: () => ({})
    }
  });

  const emit = defineEmits(['update:modelValue']);

  const model = useVModel(props, emit);
  const inputPropsResult = computed(() => $utils.Object.deepAssign({}, props.inputProps));
  const prefixIconName = computed(() => props.iconProps?.place === 'prefix' ? String(props.iconProps?.name ?? '') : '');
  const suffixIconName = computed(() => props.iconProps?.place === 'suffix' ? String(props.iconProps?.name ?? '') : '');
</script>
