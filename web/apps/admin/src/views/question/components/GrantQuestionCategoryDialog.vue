<template>
  <AppDialog v-model="visible" :modal-props="modalProps" :footer-props="footerProps">
    <div v-loading="loading" class="GrantQuestionCategoryDialog-root">
      <el-form label-position="top">
        <el-form-item label="授权用户">
          <AppSelect
            v-model="formData.userId"
            :list="userOptions"
            :select-props="{ placeholder: '请选择授权用户', filterable: true }"
            @change="dataInfo.getGrantInfo()"
          />
        </el-form-item>
        <el-form-item label="可见题库">
          <el-checkbox-group v-model="formData.categoryIds">
            <el-checkbox v-for="item in categoryOptions" :key="item.value" :label="item.value">
              {{ item.label }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
    </div>
  </AppDialog>
</template>

<script setup lang="ts" name="GrantQuestionCategoryDialog">
import { computed, reactive, toRefs, watch } from 'vue';
import $utils from '@vue-scaffold/utils';
import { useVModel } from '@vue-scaffold/hooks';
import { $apis } from '@/api/requests';

const props = defineProps<{ modelValue: boolean }>();
const emit = defineEmits<{
  'update:modelValue': [boolean];
  success: [];
}>();

const visible = useVModel(props, emit as any);

const dataInfo: any = reactive({
  formData: {
    userId: '',
    categoryIds: [] as Array<number | string>,
  },
  users: [] as Array<Record<string, any>>,
  categories: [] as Array<Record<string, any>>,
  loading: false,
  submitLoading: false,
  get userOptions() {
    return this.users.map((item: Record<string, any>) => ({
      label: `${item.fullName || item.username}（${item.username}）`,
      value: Number(item.id),
    }));
  },
  get categoryOptions() {
    return this.categories.map((item: Record<string, any>) => ({
      label: item.categoryName,
      value: Number(item.id),
    }));
  },
  async initData() {
    this.loading = true;
    try {
      const [userData, categoryData] = await Promise.all([
        $apis.users.list({ pageNum: 1, pageSize: 100, status: 1 }),
        $apis.questionCategories.listAll(),
      ]);
      this.users = userData?.records ?? [];
      this.categories = categoryData ?? [];
    } finally {
      this.loading = false;
    }
  },
  async getGrantInfo() {
    if (!this.formData.userId) {
      this.formData.categoryIds = [];
      return;
    }
    this.loading = true;
    try {
      const data = await $apis.questionCategories.grantInfo({ id: Number(this.formData.userId) });
      this.formData.categoryIds = data?.categoryIds ?? [];
    } finally {
      this.loading = false;
    }
  },
  async handleSubmit() {
    if (!this.formData.userId) {
      $utils.Message.messageAlert({ message: '请选择授权用户', type: 'warning' });
      return;
    }
    if (this.submitLoading) return;
    this.submitLoading = true;
    this.loading = true;
    try {
      await $apis.questionCategories.grant({
        userId: Number(this.formData.userId),
        categoryIds: this.formData.categoryIds.map(Number),
      });
      $utils.Message.messageAlert({ message: '授权成功' });
      visible.value = false;
      emit('success');
    } finally {
      this.submitLoading = false;
      this.loading = false;
    }
  },
  reset() {
    this.formData = { userId: '', categoryIds: [] };
  },
});

const modalProps = computed(() => ({ title: '题库授权', width: 680 }));
const footerProps = computed(() => ({
  buttons: [
    { text: '取消', close: true, buttonProps: {} },
    {
      text: dataInfo.submitLoading ? '保存中...' : '保存授权',
      close: false,
      buttonProps: { type: 'primary', loading: dataInfo.submitLoading },
      click: () => dataInfo.handleSubmit(),
    },
  ],
}));

watch(visible, (value) => {
  if (!value) {
    dataInfo.reset();
    return;
  }
  dataInfo.initData();
});

const { formData, loading, userOptions, categoryOptions } = toRefs(dataInfo);
</script>

<style scoped lang="scss">
.GrantQuestionCategoryDialog-root {
  min-height: 220px;
}
</style>
