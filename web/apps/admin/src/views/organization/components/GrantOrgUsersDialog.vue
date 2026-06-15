<template>
  <AppDialog
    v-model="visible"
    :modal-props="modalProps"
    :footer-props="footerProps"
  >
    <el-form v-loading="loading" label-position="top">
      <el-form-item label="用户列表">
        <AppSelectV2
          v-model="selectedUserIds"
          :list="users.map(item => ({ id: item.id, name: item.name }))"
          :select-v2-props="{ multiple: true, collapseTags: true, placeholder: '请选择用户' }"
        />
      </el-form-item>
    </el-form>
  </AppDialog>
</template>

<script setup lang="ts" name="GrantOrgUsersDialog">
import { computed, ref, watch } from 'vue';
import $utils from '@vue-scaffold/utils';
import { useVModel } from '@vue-scaffold/hooks';
import { $apis } from '@/api/requests';
import type { OrganizationRecord } from '@/types/domain';

export type GrantUserOption = {
  id: string;
  name: string;
};

const props = defineProps<{
  modelValue: boolean;
  organization?: OrganizationRecord | null;
  users: GrantUserOption[];
  checkedUserIds: number[];
}>();

const emit = defineEmits<{
  'update:modelValue': [boolean];
  success: [];
}>();

const visible = useVModel(props, emit as any);
const selectedUserIds = ref<string[]>([]);
const loading = ref(false);
const submitLoading = ref(false);

const modalProps = computed(() => ({
  title: `分配组织用户${props.organization ? ` - ${props.organization.orgName}` : ''}`,
  width: 680,
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

watch(
  () => props.checkedUserIds,
  value => {
    selectedUserIds.value = (value ?? []).map(item => String(item));
  },
  { immediate: true, deep: true },
);

watch(visible, value => {
  if (!value) {
    loading.value = false;
    submitLoading.value = false;
  }
});

async function handleSubmit() {
  if (!props.organization?.id || submitLoading.value) {
    return;
  }

  submitLoading.value = true;
  loading.value = true;
  try {
    await $apis.orgUsers.grant({
      orgId: Number(props.organization.id),
      userIds: selectedUserIds.value
        .map(item => Number(item))
        .filter(item => !Number.isNaN(item)),
    });
    $utils.Message.messageAlert({ message: '组织用户分配成功' });
    visible.value = false;
    emit('success');
  } finally {
    submitLoading.value = false;
    loading.value = false;
  }
}
</script>
