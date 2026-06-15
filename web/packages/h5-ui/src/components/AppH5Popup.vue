<template>
  <VanPopup v-model:show="visible" class="AppH5Popup-root" v-bind="popupPropsResult">
    <slot />
  </VanPopup>
</template>

<script setup lang="ts" name="AppH5Popup">
  import { computed, useAttrs } from 'vue';
  import { Popup as VanPopup } from 'vant';
  import $utils from '@vue-scaffold/utils';

  defineOptions({ inheritAttrs: false });

  const props = defineProps({
    show: {
      type: Boolean,
      default: false
    },
    popupProps: {
      type: Object,
      default: () => ({})
    }
  });

  const emit = defineEmits(['update:show']);
  const attrs = useAttrs();
  const visible = computed({
    get: () => props.show,
    set: value => emit('update:show', value)
  });
  const popupPropsResult = computed(() =>
    $utils.Object.deepAssign({}, attrs as Record<string, any>, props.popupProps)
  );
</script>
