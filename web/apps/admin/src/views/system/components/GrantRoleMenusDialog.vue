<template>
  <AppDialog
    v-model="visible"
    :modal-props="modalProps"
    :footer-props="footerProps"
  >
    <div v-loading="loading" class="GrantRoleMenusDialog-root">
      <el-tree
        ref="treeRef"
        :data="menus"
        node-key="id"
        show-checkbox
        default-expand-all
        :props="{ label: 'label', children: 'children' }"
      />
    </div>
  </AppDialog>
</template>

<script setup lang="ts" name="GrantRoleMenusDialog">
import { computed, nextTick, ref, watch } from 'vue';
import $utils from '@vue-scaffold/utils';
import { useVModel } from '@vue-scaffold/hooks';
import { $apis } from '@/api/requests';
import type { RoleRecord } from '@/types/domain';

export type GrantMenuTreeNode = {
  id: string;
  label: string;
  children?: GrantMenuTreeNode[];
};

const props = defineProps<{
  modelValue: boolean;
  selectItem?: RoleRecord | null;
  menus: GrantMenuTreeNode[];
  checkedMenuIds: number[];
}>();

const emit = defineEmits<{
  'update:modelValue': [boolean];
  success: [];
}>();

const treeRef = ref<any>();
const visible = useVModel(props, emit as any);
const loading = ref(false);
const submitLoading = ref(false);

const modalProps = computed(() => ({
  title: `分配角色菜单${props.selectItem ? ` - ${props.selectItem.roleName}` : ''}`,
  width: 720,
}));

const footerProps = computed(() => ({
  buttons: [
    { text: '取消', close: true, buttonProps: {} },
    {
      text: submitLoading.value ? '保存中...' : '保存',
      close: false,
      buttonProps: { type: 'primary', loading: submitLoading.value },
      click: () => handleSubmit(),
    },
  ],
}));

async function syncCheckedKeys() {
  await nextTick();
  treeRef.value?.setCheckedKeys((props.checkedMenuIds ?? []).map(item => String(item)));
}

watch(
  () => [props.modelValue, props.checkedMenuIds, props.menus] as const,
  async ([modelValue]) => {
    if (modelValue) {
      await syncCheckedKeys();
      return;
    }
    loading.value = false;
    submitLoading.value = false;
  },
  { immediate: true, deep: true },
);

async function handleSubmit() {
  if (!props.selectItem?.id || submitLoading.value) {
    return;
  }

  submitLoading.value = true;
  loading.value = true;
  try {
    const checkedKeys = (treeRef.value?.getCheckedKeys(false) ?? []) as Array<string | number>;
    const halfCheckedKeys = (treeRef.value?.getHalfCheckedKeys?.() ?? []) as Array<string | number>;
    const menuIds = [...new Set(
      [...checkedKeys, ...halfCheckedKeys]
        .map(item => Number(item))
        .filter(item => !Number.isNaN(item)),
    )];
    await $apis.roles.grantMenus({
      roleId: Number(props.selectItem.id),
      menuIds,
    });
    $utils.Message.messageAlert({ message: '角色菜单分配成功' });
    visible.value = false;
    emit('success');
  } finally {
    submitLoading.value = false;
    loading.value = false;
  }
}
</script>

<style scoped lang="scss">
.GrantRoleMenusDialog-root {
  max-height: 520px;
  overflow: auto;
  padding-right: 4px;
}
</style>
