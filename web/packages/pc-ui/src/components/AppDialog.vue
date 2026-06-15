<template>
  <el-dialog
    v-model="showModel"
    class="AppDialog-root"
    v-bind="modalPropsResult"
  >
    <slot />

    <template v-if="footerPropsResult.buttons.length > 0" #footer>
      <div class="AppDialog-footer" :style="footerPropsResult.styles">
        <AppButton
          v-for="(item, index) in footerPropsResult.buttons"
          :key="`${item.text}-${index}`"
          :button-props="item.buttonProps ?? { type: item.type ?? 'default' }"
          @click="buttonClick(item)"
        >
          {{ item.text }}
        </AppButton>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts" name="AppDialog">
import { useAttrs } from "vue";
import { modalProps, useModal } from "../composables";
import AppButton from "./AppButton.vue";

const props = defineProps({
  ...modalProps,
});

const emit = defineEmits(["update:modelValue"]);
const attrs = useAttrs();

const { showModel, modalPropsResult, footerPropsResult, buttonClick } =
  useModal(props, attrs, emit as any);
</script>

<style scoped lang="scss">
:global(.AppDialog-overlay) {
  background-color: rgba(15, 23, 42, 0.48);
  backdrop-filter: blur(5px);
}

:global(.AppDialog-root) {
  --app-dialog-radius: 22px;
  overflow: hidden;
  padding: 0;
  border: 1px solid rgba(148, 163, 184, 0.28);
  border-radius: var(--app-dialog-radius);
  background: #ffffff;
  box-shadow:
    0 28px 72px rgba(15, 23, 42, 0.24),
    0 8px 24px rgba(15, 23, 42, 0.1);
}

:global(.AppDialog-root.el-dialog) {
  margin-top: 8vh;
  padding: 0;
  border-radius: 26px;
}

:global(.AppDialog-root .el-dialog__header) {
  position: relative;
  display: flex;
  align-items: center;
  min-height: 58px;
  margin-right: 0;
  padding: 16px 56px 14px 22px;
  // border-bottom: 1px solid rgba(148, 163, 184, 0.2);
  background: linear-gradient(135deg, #eaf4ff 0%, #f3f8ff 54%, #ffffff 100%);
  box-shadow: 0 1px 0 rgba(255, 255, 255, 0.9) inset;
}

:global(.AppDialog-root .el-dialog__title) {
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
  line-height: 24px;
  letter-spacing: 0;
}

:global(.AppDialog-root .el-dialog__headerbtn) {
  display: flex;
  align-items: center;
  justify-content: center;
  top: 12px;
  right: 16px;
  width: 32px;
  height: 32px;
  border: 1px solid rgba(148, 163, 184, 0.24);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.78);
  color: #64748b;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.06);
  transition:
    transform 0.22s ease,
    background-color 0.2s ease,
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    color 0.2s ease;
}

:global(.AppDialog-root .el-dialog__headerbtn:hover) {
  border-color: rgba(37, 99, 235, 0.32);
  background: #ffffff;
  color: #0f172a;
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.16);
  transform: translateY(-1px) rotate(90deg);
}

:global(.AppDialog-root .el-dialog__headerbtn:active) {
  transform: translateY(0) rotate(90deg) scale(0.94);
}

:global(.AppDialog-root .el-dialog__body) {
  max-height: calc(100vh - 220px);
  padding: 18px 22px;
  color: #1f2937;
  overflow: auto;
}

:global(.AppDialog-root .el-dialog__body::-webkit-scrollbar) {
  width: 8px;
  height: 8px;
}

:global(.AppDialog-root .el-dialog__body::-webkit-scrollbar-thumb) {
  border: 2px solid #ffffff;
  border-radius: 999px;
  background: #cbd5e1;
}

:global(.AppDialog-root .el-dialog__footer) {
  padding: 12px 22px 16px;
  border-top: 1px solid rgba(148, 163, 184, 0.2);
  background: linear-gradient(180deg, #ffffff, #f8fafc);
  box-shadow: 0 -10px 24px rgba(15, 23, 42, 0.04);
}

.AppDialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  :global(.AppDialog-root.el-dialog) {
    width: calc(100vw - 32px) !important;
    margin-top: 5vh;
  }

  :global(.AppDialog-root .el-dialog__header) {
    min-height: 54px;
    padding: 14px 50px 12px 18px;
  }

  :global(.AppDialog-root .el-dialog__body) {
    max-height: calc(100vh - 190px);
    padding: 16px;
  }

  :global(.AppDialog-root .el-dialog__footer) {
    padding: 12px 16px 14px;
  }
}
</style>
