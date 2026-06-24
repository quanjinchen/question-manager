<template>
  <main class="QuestionBankCategory-root">
    <AppTableList>
      <AppListHeader>
        <el-row :gutter="16" style="width: 100%">
          <el-col :xs="12" :sm="8" :md="6" :lg="5">
            <AppInput
              v-model="searchParams.categoryName"
              placeholder="请输入分类名称"
              :icon-props="{ place: 'suffix', name: 'Search' }"
              :input-props="{ clearable: true }"
              @input="dataInfo.debounceSearch()"
            />
          </el-col>
          <el-col :xs="12" :sm="8" :md="5" :lg="4">
            <AppSelect v-model="searchParams.status" :list="statusOptions" :select-props="{ placeholder: '请选择状态', clearable: true }" @change="dataInfo.search()" />
          </el-col>
          <el-col :xs="24" :sm="8" :md="13" :lg="15">
            <div class="header-handle">
              <AppButton :button-props="{ loading }" @click="dataInfo.getList()">刷新</AppButton>
              <AppButton :button-props="{ type: 'primary' }" v-permission="'question:bankCategory:update'" @click="dataInfo.openCreate()">新增分类</AppButton>
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
    <QuestionBankCategoryFormDialog v-model="dataInfo.dialogVisible" :select-item="dataInfo.selectedRecord" @success="dataInfo.getList()" />
  </main>
</template>

<script setup lang="ts" name="QuestionBankCategory">
import { reactive, toRefs } from 'vue';
import $utils from '@vue-scaffold/utils';
import { $apis } from '@/api/requests';
import type { QuestionBankCategoryRecord } from '@/types/domain';
import QuestionBankCategoryFormDialog from '@/views/question/components/QuestionBankCategoryFormDialog.vue';
import tableInfo from '@/views/question/tables/QuestionBankCategory';

const statusOptions = [
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 },
];

const dataInfo: any = reactive({
  pageInfo: { pageNum: 1, pageSize: 10 },
  searchParams: { categoryName: '', status: '' },
  list: [] as QuestionBankCategoryRecord[],
  total: 0,
  loading: false,
  actionLoading: false,
  dialogVisible: false,
  selectedRecord: null as QuestionBankCategoryRecord | null,
  get params() {
    return { ...this.searchParams, ...this.pageInfo };
  },
  async getList() {
    this.loading = true;
    try {
      const data = await $apis.questionBankCategories.list(this.params);
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
  openEdit(row: QuestionBankCategoryRecord) {
    this.selectedRecord = row;
    this.dialogVisible = true;
  },
  async deleteCategory(row: QuestionBankCategoryRecord) {
    if (this.actionLoading) return;
    this.actionLoading = true;
    try {
      await $utils.Message.messageConfirm(`确认删除题库分类"${row.categoryName}"吗？`);
      await $apis.questionBankCategories.delete({ categoryId: Number(row.id) });
      $utils.Message.messageAlert({ message: '删除成功' });
      await this.getList();
    } finally {
      this.actionLoading = false;
    }
  },
  async handleAction(row: QuestionBankCategoryRecord, action: Record<string, any>) {
    const actionMap: Record<string, () => void | Promise<void>> = {
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
.QuestionBankCategory-root {
  height: 100%;
}

.header-handle {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}
</style>
