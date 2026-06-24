<template>
  <main class="QuestionItem-root">
    <AppTableList>
      <AppListHeader>
        <el-row :gutter="16" style="width: 100%">
          <el-col :xs="12" :sm="8" :md="5">
            <AppSelect v-model="searchParams.categoryId" :list="categoryOptions" :select-props="{ placeholder: '请选择所属分类' }" @change="dataInfo.search()" />
          </el-col>
          <el-col :xs="12" :sm="8" :md="5">
            <AppSelect v-model="searchParams.questionType" :list="questionTypeOptions" :select-props="{ placeholder: '请选择题型' }" @change="dataInfo.search()" />
          </el-col>
          <el-col :xs="12" :sm="8" :md="5">
            <AppInput v-model="searchParams.title" placeholder="请输入题干" :icon-props="{ place: 'suffix', name: 'Search' }" @input="dataInfo.debounceSearch()" />
          </el-col>
          <el-col :xs="12" :sm="24" :md="9">
            <div class="header-handle">
              <AppButton :button-props="{ loading }" @click="dataInfo.getList()">刷新</AppButton>
              <AppButton :button-props="{ type: 'primary' }" v-permission="'question:item:update'" @click="dataInfo.openCreate()">新增题目</AppButton>
            </div>
          </el-col>
        </el-row>
      </AppListHeader>
      <AppTable :table-props="{ data: list }" :table-info="tableInfo" :page-info="pageInfo" :loading="loading" @handle-click="dataInfo.handleAction">
        <template #questionType="{ row }">
          <el-tag>{{ questionTypeMap[row.questionType] || row.questionType }}</el-tag>
        </template>
        <template #status="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </AppTable>
      <AppPager v-model:page-index="pageInfo.pageNum" v-model:page-size="pageInfo.pageSize" :total="total" @change="dataInfo.getList()" />
    </AppTableList>
    <QuestionFormDialog v-model="dataInfo.dialogVisible" :select-item="dataInfo.selectedRecord" @success="dataInfo.getList()" />
  </main>
</template>

<script setup lang="ts" name="QuestionItem">
import { reactive, toRefs } from 'vue';
import $utils from '@vue-scaffold/utils';
import { $apis } from '@/api/requests';
import type { QuestionRecord } from '@/types/domain';
import QuestionFormDialog from '@/views/question/components/QuestionFormDialog.vue';
import tableInfo from '@/views/question/tables/QuestionItem';

const questionTypeMap: Record<string, string> = { SINGLE: '单选题', MULTIPLE: '多选题', JUDGE: '判断题', QA: '问答题' };
const questionTypeOptions = [
  { label: '单选题', value: 'SINGLE' },
  { label: '多选题', value: 'MULTIPLE' },
  { label: '判断题', value: 'JUDGE' },
  { label: '问答题', value: 'QA' },
];

const dataInfo: any = reactive({
  pageInfo: { pageNum: 1, pageSize: 10 },
  searchParams: { categoryId: '', questionType: '', title: '' },
  categories: [] as Array<Record<string, any>>,
  list: [] as QuestionRecord[],
  total: 0,
  loading: false,
  actionLoading: false,
  dialogVisible: false,
  selectedRecord: null as QuestionRecord | null,
  get categoryOptions() {
    return this.categories.map((item: Record<string, any>) => ({ label: item.categoryName, value: Number(item.id) }));
  },
  get params() {
    return { ...this.searchParams, ...this.pageInfo };
  },
  async initData() {
    this.loading = true;
    try {
      this.categories = await $apis.questionCategories.listAll();
      await this.getList();
    } finally {
      this.loading = false;
    }
  },
  async getList() {
    this.loading = true;
    try {
      const data = await $apis.questions.list(this.params);
      this.total = data?.total ?? 0;
      this.list = data?.records ?? [];
    } finally {
      this.loading = false;
    }
  },
  search() {
    this.pageInfo.pageNum = 1;
    this.getList();
  },
  debounceSearch: $utils.Tool.debounce(function (this: any) {
    this.search();
  }, 300),
  openCreate() {
    this.selectedRecord = null;
    this.dialogVisible = true;
  },
  openEdit(row: QuestionRecord) {
    this.selectedRecord = row;
    this.dialogVisible = true;
  },
  async deleteQuestion(row: QuestionRecord) {
    if (this.actionLoading) return;
    this.actionLoading = true;
    try {
      await $utils.Message.messageConfirm('确认删除该题目吗？');
      await $apis.questions.delete({ questionId: Number(row.id) });
      $utils.Message.messageAlert({ message: '删除成功' });
      await this.getList();
    } finally {
      this.actionLoading = false;
    }
  },
  async handleAction(row: QuestionRecord, action: Record<string, any>) {
    const actionMap: Record<string, () => void | Promise<void>> = {
      edit: () => dataInfo.openEdit(row),
      delete: () => dataInfo.deleteQuestion(row),
    };
    await actionMap[action.key]?.();
  },
});

const { pageInfo, searchParams, list, total, loading, categoryOptions } = toRefs(dataInfo);
dataInfo.initData();
</script>

<style scoped lang="scss">
.QuestionItem-root {
  height: 100%;
}

.header-handle {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}
</style>
