import { computed } from 'vue';
import $utils from '@vue-scaffold/utils';

export const loadingAttrs = {
  'element-loading-text': '加载中...',
  'element-loading-svg-view-box': '-10, -10, 50, 50',
  'element-loading-svg': `
  <path class="path" d="
    M 30 15
    L 28 17
    M 25.61 25.61
    A 15 15, 0, 0, 1, 15 30
    A 15 15, 0, 1, 1, 27.99 7.5
    L 15 15
  " style="stroke-width: 4px; fill: rgba(0, 0, 0, 0)"/>
`
};

export const modalProps = {
  modelValue: { type: Boolean, default: false },
  teleportProps: { type: Object, default: () => ({}) },
  modalProps: { type: Object, default: () => ({}) },
  footerProps: { type: Object, default: () => ({}) },
  modalGenre: { type: String, default: 'dialog' }
};

export function useModal(
  props: Record<string, any>,
  attrs: Record<string, unknown>,
  emit: (event: string, value: unknown) => void,
  defaultProps: Record<string, any> = {}
) {
  const showModel = computed({
    get: () => props.modelValue,
    set: value => emit('update:modelValue', value)
  });

  const teleportPropsResult = computed(() =>
    $utils.Object.deepAssign(
      { to: 'body' },
      defaultProps.teleportProps ?? {},
      props.teleportProps ?? {}
    )
  );

  const modalPropsResult = computed(() => {
    const defaults = props.modalGenre === 'drawer' ? { size: '32%' } : {};
    return $utils.Object.deepAssign(
      {
        title: '弹窗',
        width: 520,
        draggable: true,
        closeOnClickModal: false,
        closeOnPressEscape: false,
        class: 'AppDialog-root',
        modalClass: 'AppDialog-overlay'
      },
      defaults,
      attrs as Record<string, any>,
      defaultProps.modalProps ?? {},
      props.modalProps ?? {}
    );
  });

  const footerPropsResult = computed(() => {
    const buttons = (props.footerProps?.buttons ?? defaultProps.footerProps?.buttons ?? []).map((item: any) => {
      if (typeof item === 'string') {
        return item === 'cancel'
          ? { type: 'default', text: '取消', close: true }
          : { type: 'primary', text: '确认', close: false };
      }
      return item;
    });

    return {
      styles: $utils.Tool.getCssObject({
        ...(defaultProps.footerProps?.styles ?? {}),
        ...(props.footerProps?.styles ?? {})
      }),
      buttons
    };
  });

  function buttonClick(item: Record<string, any>) {
    item.click?.();
    if (item.close) {
      showModel.value = false;
    }
  }

  return {
    showModel,
    teleportPropsResult,
    modalPropsResult,
    footerPropsResult,
    buttonClick
  };
}
