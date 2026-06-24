<template>
  <AppDialog v-model="visible" :modal-props="modalProps" :footer-props="footerProps">
    <el-form ref="formRef" v-loading="loading" :model="formData" :rules="dataInfo.rules" label-position="top">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="分类名称" prop="categoryName">
            <AppInput v-model="formData.categoryName" v-trim placeholder="请输入分类名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <AppSelect v-model="formData.status" :list="statusOptions" :select-props="{ placeholder: '请选择状态' }" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="排序值" prop="sortOrder">
            <el-input-number v-model="formData.sortOrder" :min="0" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="分类描述" prop="description">
            <AppInput v-model="formData.description" placeholder="请输入分类描述" :input-props="{ type: 'textarea', rows: 3 }" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </AppDialog>
</template>

<script setup lang="ts" name="QuestionBankCategoryFormDialog">
import { computed, reactive, ref, toRefs, watch } from 'vue';
import type { FormInstance } from 'element-plus';
import $utils from '@vue-scaffold/utils';
import { useVModel } from '@vue-scaffold/hooks';
import { $apis } from '@/api/requests';
import type { QuestionBankCategoryRecord } from '@/types/domain';

const statusOptions = [
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 },
];

const props = defineProps<{
  modelValue: boolean;
  selectItem?: QuestionBankCategoryRecord | null;
}>();

const emit = defineEmits<{
  'update:modelValue': [boolean];
  success: [];
}>();

const formRef = ref<FormInstance>();
const visible = useVModel(props, emit as any);

const dataInfo: any = reactive({
  formData: {
    categoryName: '',
    description: '',
    sortOrder: 0,
    status: 1,
  },
  rules: {
    categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }],
  },
  loading: false,
  submitLoading: false,
  get isEdit() {
    return Boolean(props.selectItem?.id);
  },
  initForm() {
    this.formData = { categoryName: '', description: '', sortOrder: 0, status: 1 };
    formRef.value?.resetFields();
    formRef.value?.clearValidate();
  },
  async getDetail() {
    if (!props.selectItem?.id) return;
    this.loading = true;
    try {
      const detail = await $apis.questionBankCategories.detail({ id: Number(props.selectItem.id) });
      this.formData = { categoryName: '', description: '', sortOrder: 0, status: 1, ...detail };
    } finally {
      this.loading = false;
    }
  },
  async handleSubmit() {
    await formRef.value?.validate();
    if (this.submitLoading) return;
    this.submitLoading = true;
    this.loading = true;
    try {
      await $apis.questionBankCategories[this.isEdit ? 'update' : 'create']({ ...this.formData });
      $utils.Message.messageAlert({ message: '操作成功' });
      visible.value = false;
      emit('success');
    } finally {
      this.submitLoading = false;
      this.loading = false;
    }
  },
});

const modalProps = computed(() => ({
  title: `${dataInfo.isEdit ? '编辑' : '新增'}题库分类`,
  width: 680,
}));

const footerProps = computed(() => ({
  buttons: [
    { text: '取消', close: true, buttonProps: {} },
    {
      text: dataInfo.submitLoading ? '保存中...' : '保存',
      close: false,
      buttonProps: { type: 'primary', loading: dataInfo.submitLoading },
      click: () => dataInfo.handleSubmit(),
    },
  ],
}));

watch(visible, (value) => {
  if (!value) {
    dataInfo.initForm();
    return;
  }
  dataInfo.getDetail();
});

const { formData, loading } = toRefs(dataInfo);
</script>
