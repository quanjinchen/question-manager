<template>
  <VanField v-model="model" class="AppH5Field-root" v-bind="fieldPropsResult">
    <template v-if="$slots.label" #label>
      <slot name="label" />
    </template>
    <template v-if="$slots.input" #input>
      <slot name="input" />
    </template>
    <template v-if="$slots.button" #button>
      <slot name="button" />
    </template>
    <template v-if="$slots.leftIcon" #left-icon>
      <slot name="leftIcon" />
    </template>
    <template v-if="$slots.rightIcon" #right-icon>
      <slot name="rightIcon" />
    </template>
    <template v-if="$slots.extra" #extra>
      <slot name="extra" />
    </template>
  </VanField>
</template>

<script setup lang="ts" name="AppH5Field">
  import { computed, useAttrs } from 'vue';
  import { Field as VanField } from 'vant';
  import { useVModel } from '@vue-scaffold/hooks';
  import $utils from '@vue-scaffold/utils';

  defineOptions({ inheritAttrs: false });

  const props = defineProps({
    modelValue: {
      type: [String, Number],
      default: ''
    },
    fieldProps: {
      type: Object,
      default: () => ({})
    }
  });

  const emit = defineEmits(['update:modelValue']);
  const attrs = useAttrs();
  const model = useVModel(props, emit);
  const fieldPropsResult = computed(() =>
    $utils.Object.deepAssign({}, attrs as Record<string, any>, props.fieldProps)
  );
</script>
