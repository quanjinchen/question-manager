<template>
  <AppDialog v-model="visible" :modal-props="modalProps" :footer-props="footerProps">
    <el-form ref="formRef" v-loading="loading" :model="formData" :rules="dataInfo.rules" label-position="top">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="题库名称" prop="categoryName">
            <AppInput v-model="formData.categoryName" v-trim placeholder="请输入题库名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="题库分类" prop="bankCategoryId">
            <AppSelect v-model="formData.bankCategoryId" :list="bankCategoryOptions" :select-props="{ placeholder: '请选择题库分类' }" />
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
          <el-form-item label="题库描述" prop="description">
            <AppInput v-model="formData.description" placeholder="请输入题库描述" :input-props="{ type: 'textarea', rows: 3 }" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </AppDialog>
</template>

<script setup lang="ts" name="QuestionCategoryFormDialog">
import { computed, reactive, ref, toRefs, watch } from 'vue';
import type { FormInstance } from 'element-plus';
import $utils from '@vue-scaffold/utils';
import { useVModel } from '@vue-scaffold/hooks';
import { $apis } from '@/api/requests';
import type { QuestionCategoryRecord } from '@/types/domain';

const statusOptions = [
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 },
];

const props = defineProps<{
  modelValue: boolean;
  selectItem?: QuestionCategoryRecord | null;
  defaultBankCategoryId?: string | number;
}>();

const emit = defineEmits<{
  'update:modelValue': [boolean];
  success: [];
}>();

const formRef = ref<FormInstance>();
const visible = useVModel(props, emit as any);

const emptyForm = () => ({
  bankCategoryId: '' as string | number,
  categoryName: '',
  description: '',
  sortOrder: 0,
  status: 1,
});

const dataInfo: any = reactive({
  formData: emptyForm(),
  bankCategories: [] as Array<Record<string, any>>,
  rules: {
    bankCategoryId: [{ required: true, message: '请选择题库分类', trigger: 'change' }],
    categoryName: [{ required: true, message: '请输入题库名称', trigger: 'blur' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }],
  },
  loading: false,
  submitLoading: false,
  get isEdit() {
    return Boolean(props.selectItem?.id);
  },
  get bankCategoryOptions() {
    return this.bankCategories.map((item: Record<string, any>) => ({ label: item.categoryName, value: Number(item.id) }));
  },
  initForm() {
    this.formData = { ...emptyForm(), bankCategoryId: props.defaultBankCategoryId || '' };
    formRef.value?.resetFields();
    formRef.value?.clearValidate();
  },
  async initData() {
    this.loading = true;
    try {
      this.bankCategories = await $apis.questionBankCategories.listAll();
      await this.getDetail();
    } finally {
      this.loading = false;
    }
  },
  async getDetail() {
    if (!props.selectItem?.id) {
      this.formData = { ...emptyForm(), bankCategoryId: props.defaultBankCategoryId || '' };
      return;
    }
    const detail = await $apis.questionCategories.detail({ id: Number(props.selectItem.id) });
    this.formData = { ...emptyForm(), ...detail };
  },
  async handleSubmit() {
    await formRef.value?.validate();
    if (this.submitLoading) return;
    this.submitLoading = true;
    this.loading = true;
    try {
      await $apis.questionCategories[this.isEdit ? 'update' : 'create']({ ...this.formData });
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
  title: `${dataInfo.isEdit ? '编辑' : '新增'}题库`,
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
  dataInfo.initData();
});

const { formData, loading, bankCategoryOptions } = toRefs(dataInfo);
</script>
