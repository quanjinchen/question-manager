<template>
  <AppDialog
    v-model="visible"
    :modal-props="modalProps"
    :footer-props="footerProps"
  >
    <el-form
      ref="formRef"
      v-loading="loading"
      :model="formData"
      :rules="dataInfo.rules"
      label-position="top"
    >
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="角色名称" prop="roleName">
            <AppInput v-model="formData.roleName" v-trim placeholder="请输入角色名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="角色编码" prop="roleCode">
            <AppInput
              v-model="formData.roleCode"
              v-trim
              placeholder="请输入角色编码"
              :input-props="{ disabled: dataInfo.isEdit && formData.systemDefault }"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="是否默认角色" prop="systemDefault">
            <AppSwitch v-model="formData.systemDefault" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="备注" prop="remark">
            <AppInput
              v-model="formData.remark"
              placeholder="请输入备注"
              :input-props="{ type: 'textarea', rows: 3 }"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </AppDialog>
</template>

<script setup lang="ts" name="RoleFormDialog">
import { computed, reactive, ref, toRefs, watch } from 'vue';
import $utils from '@vue-scaffold/utils';
import { useVModel } from '@vue-scaffold/hooks';
import type { FormInstance } from 'element-plus';
import { $apis } from '@/api/requests';
import type { RoleRecord } from '@/types/domain';

type RoleFormData = Omit<RoleRecord, 'id' | 'createdAt' | 'updatedAt' | 'userNum' | 'userGroupNum'>;

const props = defineProps<{
  modelValue: boolean;
  selectItem?: RoleRecord | null;
}>();

const emit = defineEmits<{
  'update:modelValue': [boolean];
  success: [];
}>();

const formRef = ref<FormInstance>();
const visible = useVModel(props, emit as any);

const modalProps = computed(() => ({
  title: `${dataInfo.isEdit ? '编辑' : '新增'}角色`,
  width: 640,
}));

const footerProps = computed(() => ({
  buttons: [
    { text: '取消', close: true, buttonProps: {} },
    {
      text: submitLoading.value ? '保存中...' : '保存',
      close: false,
      buttonProps: { type: 'primary', loading: submitLoading.value },
      click: () => dataInfo.handleSubmit(),
    },
  ],
}));

const dataInfo = reactive({
  formData: {
    roleCode: '',
    roleName: '',
    systemDefault: false,
    remark: '',
  } as RoleFormData,
  rules: {
    roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
    roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  },
  loading: false,
  submitLoading: false,
  get isEdit() {
    return Boolean(props.selectItem?.id);
  },
  initForm() {
    this.formData = {
      roleCode: '',
      roleName: '',
      systemDefault: false,
      remark: '',
    };
    formRef.value?.clearValidate();
  },
  async getDetail() {
    if (!props.selectItem?.id) {
      return;
    }

    this.loading = true;
    try {
      const detail = await $apis.roles.detail({
        id: Number(props.selectItem.id),
      });
      this.formData = mapRoleDetail(detail);
    } finally {
      this.loading = false;
    }
  },
  get params() {
    return {
      id: this.isEdit ? Number(props.selectItem?.id) : undefined,
      code: this.formData.roleCode,
      name: this.formData.roleName,
      status: 1,
      remark: this.formData.remark ?? '',
    };
  },
  async handleSubmit() {
    await formRef.value?.validate();
    if (this.submitLoading) {
      return;
    }

    this.submitLoading = true;
    this.loading = true;
    try {
      await $apis.roles[this.isEdit ? 'update' : 'save'](this.params);
      $utils.Message.messageAlert({ message: this.isEdit ? '角色更新成功' : '角色创建成功' });
      visible.value = false;
      emit('success');
    } finally {
      this.submitLoading = false;
      this.loading = false;
    }
  },
});

watch(visible, value => {
  if (!value) {
    dataInfo.initForm();
    dataInfo.loading = false;
    dataInfo.submitLoading = false;
    return;
  }
  dataInfo.getDetail();
});

function mapRoleDetail(detail: Record<string, any>): RoleFormData {
  return {
    roleCode: detail.code ?? detail.roleCode ?? '',
    roleName: detail.name ?? detail.roleName ?? '',
    systemDefault: Boolean(detail.systemDefault),
    remark: detail.remark ?? '',
  };
}

const { formData, loading, submitLoading } = toRefs(dataInfo);
</script>
