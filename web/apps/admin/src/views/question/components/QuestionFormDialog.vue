<template>
  <AppDialog v-model="visible" :modal-props="modalProps" :footer-props="footerProps">
    <el-form ref="formRef" v-loading="loading" :model="formData" :rules="dataInfo.rules" label-position="top">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="所属题库" prop="categoryId">
            <AppSelect v-model="formData.categoryId" :list="categoryOptions" :select-props="{ placeholder: '请选择所属题库' }" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="题型" prop="questionType">
            <AppSelect v-model="formData.questionType" :list="questionTypeOptions" :select-props="{ placeholder: '请选择题型' }" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="题干" prop="title">
            <AppInput v-model="formData.title" placeholder="请输入题干" :input-props="{ type: 'textarea', rows: 3 }" />
          </el-form-item>
        </el-col>
        <el-col v-if="dataInfo.hasOptions" :span="24">
          <el-form-item label="选项" prop="optionsJson">
            <div class="option-list">
              <div v-for="item in optionList" :key="item.label" class="option-row">
                <span class="option-label">{{ item.label }}</span>
                <AppInput v-model="item.content" :placeholder="`请输入${item.label}选项内容`" />
                <AppButton
                  v-if="dataInfo.canEditOptions"
                  :button-props="{ type: 'danger', link: true, disabled: optionList.length <= 2 }"
                  @click="dataInfo.removeOption(item.label)"
                >
                  删除
                </AppButton>
              </div>
              <AppButton v-if="dataInfo.canEditOptions" @click="dataInfo.addOption()">添加选项</AppButton>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="正确答案/参考答案" prop="answer">
            <template v-if="formData.questionType === 'SINGLE' || formData.questionType === 'JUDGE'">
              <AppSelect v-model="formData.answer" :list="answerOptions" :select-props="{ placeholder: '请选择正确答案' }" />
            </template>
            <template v-else-if="formData.questionType === 'MULTIPLE'">
              <el-select v-model="multipleAnswer" multiple clearable placeholder="请选择正确答案" style="width: 100%">
                <el-option v-for="item in answerOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </template>
            <template v-else>
              <AppInput v-model="formData.answer" placeholder="请输入参考答案" :input-props="{ type: 'textarea', rows: 4 }" />
            </template>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="分值" prop="score">
            <el-input-number v-model="formData.score" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="排序值" prop="sortOrder">
            <el-input-number v-model="formData.sortOrder" :min="0" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <AppSelect v-model="formData.status" :list="statusOptions" :select-props="{ placeholder: '请选择状态' }" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="答案解析" prop="analysis">
            <AppInput v-model="formData.analysis" placeholder="请输入答案解析" :input-props="{ type: 'textarea', rows: 3 }" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </AppDialog>
</template>

<script setup lang="ts" name="QuestionFormDialog">
import { computed, nextTick, reactive, ref, toRefs, watch } from 'vue';
import type { FormInstance } from 'element-plus';
import $utils from '@vue-scaffold/utils';
import { useVModel } from '@vue-scaffold/hooks';
import { $apis } from '@/api/requests';
import type { QuestionRecord } from '@/types/domain';

const statusOptions = [
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 },
];
const questionTypeOptions = [
  { label: '单选题', value: 'SINGLE' },
  { label: '多选题', value: 'MULTIPLE' },
  { label: '判断题', value: 'JUDGE' },
  { label: '问答题', value: 'QA' },
];
const defaultOptions = () => ['A', 'B', 'C', 'D'].map(label => ({ label, content: '' }));
const judgeOptions = () => [
  { label: 'A', content: '正确' },
  { label: 'B', content: '错误' },
];
const OPTION_LABELS = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');

const props = defineProps<{
  modelValue: boolean;
  selectItem?: QuestionRecord | null;
  defaultCategoryId?: string | number;
}>();

const emit = defineEmits<{
  'update:modelValue': [boolean];
  success: [];
}>();

const formRef = ref<FormInstance>();
const visible = useVModel(props, emit as any);

const dataInfo: any = reactive({
  formData: {
    categoryId: '' as string | number,
    questionType: 'SINGLE',
    title: '',
    optionsJson: '',
    answer: '',
    analysis: '',
    score: 1,
    sortOrder: 0,
    status: 1,
  },
  optionList: defaultOptions(),
  multipleAnswer: [] as string[],
  categories: [] as Array<Record<string, any>>,
  syncingDetail: false,
  rules: {
    categoryId: [{ required: true, message: '请选择所属题库', trigger: 'change' }],
    questionType: [{ required: true, message: '请选择题型', trigger: 'change' }],
    title: [{ required: true, message: '请输入题干', trigger: 'blur' }],
    answer: [{ required: true, message: '请输入正确答案或参考答案', trigger: 'blur' }],
  },
  loading: false,
  submitLoading: false,
  get isEdit() {
    return Boolean(props.selectItem?.id);
  },
  get isChoiceQuestion() {
    return this.formData.questionType === 'SINGLE' || this.formData.questionType === 'MULTIPLE';
  },
  get isJudgeQuestion() {
    return this.formData.questionType === 'JUDGE';
  },
  get hasOptions() {
    return this.isChoiceQuestion || this.isJudgeQuestion;
  },
  get canEditOptions() {
    return this.isChoiceQuestion;
  },
  get categoryOptions() {
    return this.categories.map((item: Record<string, any>) => ({ label: item.categoryName, value: Number(item.id) }));
  },
  get answerOptions() {
    return this.optionList.map((item: Record<string, any>) => ({ label: item.label, value: item.label }));
  },
  initForm() {
    this.formData = { categoryId: props.defaultCategoryId || '', questionType: 'SINGLE', title: '', optionsJson: '', answer: '', analysis: '', score: 1, sortOrder: 0, status: 1 };
    this.optionList = defaultOptions();
    this.multipleAnswer = [];
    formRef.value?.resetFields();
    formRef.value?.clearValidate();
  },
  resetOptionsByType() {
    this.optionList = this.isJudgeQuestion ? judgeOptions() : defaultOptions();
    this.multipleAnswer = [];
    this.formData.answer = this.isJudgeQuestion ? 'A' : '';
  },
  addOption() {
    const nextLabel = OPTION_LABELS[this.optionList.length];
    if (!nextLabel) {
      $utils.Message.messageAlert({ message: '最多支持 26 个选项', type: 'warning' });
      return;
    }
    this.optionList.push({ label: nextLabel, content: '' });
  },
  removeOption(label: string) {
    if (this.optionList.length <= 2) {
      return;
    }
    this.optionList = this.optionList.filter((item: Record<string, any>) => item.label !== label)
      .map((item: Record<string, any>, index: number) => ({
        label: OPTION_LABELS[index],
        content: String(item.content ?? ''),
      }));
    this.formData.answer = '';
    this.multipleAnswer = [];
  },
  async initData() {
    this.loading = true;
    try {
      this.categories = await $apis.questionCategories.listAll();
      await this.getDetail();
    } finally {
      this.loading = false;
    }
  },
  async getDetail() {
    if (!props.selectItem?.id) {
      this.formData.categoryId = props.defaultCategoryId || '';
      return;
    }
    const detail = await $apis.questions.detail({ id: Number(props.selectItem.id) });
    this.syncingDetail = true;
    this.formData = { ...this.formData, ...detail };
    try {
      this.optionList = detail.optionsJson ? JSON.parse(detail.optionsJson) : (detail.questionType === 'JUDGE' ? judgeOptions() : defaultOptions());
    } catch (_error) {
      this.optionList = defaultOptions();
    }
    this.multipleAnswer = detail.questionType === 'MULTIPLE' && detail.answer ? detail.answer.split(',') : [];
    await nextTick();
    this.syncingDetail = false;
  },
  buildOptionsJson() {
    return JSON.stringify(this.optionList.filter((item: Record<string, any>) => item.content));
  },
  async handleSubmit() {
    if (this.formData.questionType === 'MULTIPLE') {
      this.formData.answer = this.multipleAnswer.join(',');
    }
    if (this.hasOptions) {
      this.formData.optionsJson = this.buildOptionsJson();
      if (!this.formData.optionsJson || this.formData.optionsJson === '[]') {
        $utils.Message.messageAlert({ message: '请输入选项内容', type: 'warning' });
        return;
      }
    }
    await formRef.value?.validate();
    if (this.submitLoading) return;
    this.submitLoading = true;
    this.loading = true;
    try {
      await $apis.questions[this.isEdit ? 'update' : 'create']({ ...this.formData });
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
  title: `${dataInfo.isEdit ? '编辑' : '新增'}题目`,
  width: 860,
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

watch(() => dataInfo.formData.questionType, (value) => {
  if (dataInfo.syncingDetail) {
    return;
  }
  if (value !== 'MULTIPLE') {
    dataInfo.multipleAnswer = [];
  }
  if (visible.value) {
    dataInfo.resetOptionsByType();
  }
});

const { formData, optionList, multipleAnswer, loading, categoryOptions, answerOptions } = toRefs(dataInfo);
</script>

<style scoped lang="scss">
.option-list {
  display: grid;
  gap: 8px;
  width: 100%;
}

.option-row {
  display: grid;
  grid-template-columns: 32px 1fr 56px;
  align-items: center;
  gap: 8px;
}

.option-label {
  color: #606266;
  font-weight: 600;
}
</style>
