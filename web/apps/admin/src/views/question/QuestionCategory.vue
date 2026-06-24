<template>
  <main class="QuestionCategory-root">
    <AppTableList>
      <AppListHeader>
        <el-row :gutter="16" style="width: 100%">
          <el-col :xs="12" :sm="8" :md="6" :lg="5">
            <AppInput v-model="searchParams.categoryName" placeholder="请输入分类名称" :icon-props="{ place: 'suffix', name: 'Search' }" @input="dataInfo.debounceSearch()" />
          </el-col>
          <el-col :xs="12" :sm="8" :md="5" :lg="4">
            <AppSelect v-model="searchParams.status" :list="statusOptions" :select-props="{ placeholder: '请选择状态' }" @change="dataInfo.search()" />
          </el-col>
          <el-col :xs="24" :sm="8" :md="13" :lg="15">
            <div class="header-handle">
              <AppButton :button-props="{ loading }" @click="dataInfo.getList()">刷新</AppButton>
              <AppButton :button-props="{ type: 'primary' }" v-permission="'question:category:update'" @click="dataInfo.openCreate()">新增分类</AppButton>
            </div>
          </el-col>
        </el-row>
      </AppListHeader>
      <AppTable :table-props="{ data: list }" :table-info="tableInfo" :page-info="pageInfo" :loading="loading" @handle-click="dataInfo.handleAction">
        <template #status="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </AppTable>
      <AppPager v-model:page-index="pageInfo.pageNum" v-model:page-size="pageInfo.pageSize" :total="total" @change="dataInfo.getList()" />
    </AppTableList>
    <QuestionCategoryFormDialog v-model="dataInfo.dialogVisible" :select-item="dataInfo.selectedRecord" @success="dataInfo.getList()" />
    <GrantQuestionCategoryDialog v-model="dataInfo.grantDialogVisible" />
  </main>
</template>

<script setup lang="ts" name="QuestionCategory">
import { reactive, toRefs } from 'vue';
import $utils from '@vue-scaffold/utils';
import { $apis } from '@/api/requests';
import type { QuestionCategoryRecord } from '@/types/domain';
import QuestionCategoryFormDialog from '@/views/question/components/QuestionCategoryFormDialog.vue';
import GrantQuestionCategoryDialog from '@/views/question/components/GrantQuestionCategoryDialog.vue';
import tableInfo from '@/views/question/tables/QuestionCategory';

const statusOptions = [
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 },
];

const dataInfo: any = reactive({
  pageInfo: { pageNum: 1, pageSize: 10 },
  searchParams: { categoryName: '', status: '' },
  list: [] as QuestionCategoryRecord[],
  total: 0,
  loading: false,
  actionLoading: false,
  dialogVisible: false,
  grantDialogVisible: false,
  selectedRecord: null as QuestionCategoryRecord | null,
  get params() {
    return { ...this.searchParams, ...this.pageInfo };
  },
  async getList() {
    this.loading = true;
    try {
      const data = await $apis.questionCategories.list(this.params);
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
  openEdit(row: QuestionCategoryRecord) {
    this.selectedRecord = row;
    this.dialogVisible = true;
  },
  openGrant() {
    this.grantDialogVisible = true;
  },
  async deleteCategory(row: QuestionCategoryRecord) {
    if (this.actionLoading) return;
    this.actionLoading = true;
    try {
      await $utils.Message.messageConfirm(`确认删除题目分类"${row.categoryName}"吗？`);
      await $apis.questionCategories.delete({ categoryId: Number(row.id) });
      $utils.Message.messageAlert({ message: '删除成功' });
      await this.getList();
    } finally {
      this.actionLoading = false;
    }
  },
  async handleAction(row: QuestionCategoryRecord, action: Record<string, any>) {
    const actionMap: Record<string, () => void | Promise<void>> = {
      grant: () => dataInfo.openGrant(),
      edit: () => dataInfo.openEdit(row),
      delete: () => dataInfo.deleteCategory(row),
    };
    await actionMap[action.key]?.();
  },
});

const { pageInfo, searchParams, list, total, loading } = toRefs(dataInfo);
dataInfo.getList();
</script>

<style scoped lang="scss">
.QuestionCategory-root {
  height: 100%;
}

.header-handle {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}
</style>
