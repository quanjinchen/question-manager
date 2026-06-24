<template>
  <main class="QuestionCategory-root">
    <section class="category-pane">
      <div class="pane-header">
        <div>
          <h2>题库分类</h2>
          <span>{{ bankCategoryTotal }} 个分类</span>
        </div>
        <div class="pane-actions">
          <AppButton :button-props="{ loading: categoryLoading }" @click="dataInfo.getBankCategoryList()">刷新</AppButton>
          <AppButton :button-props="{ type: 'primary' }" v-permission="'question:bankCategory:update'" @click="dataInfo.openBankCategoryCreate()">
            新增
          </AppButton>
        </div>
      </div>

      <div class="category-filters">
        <AppInput
          v-model="bankCategorySearchParams.categoryName"
          placeholder="搜索分类"
          :icon-props="{ place: 'suffix', name: 'Search' }"
          :input-props="{ clearable: true }"
          @input="dataInfo.debounceBankCategorySearch()"
        />
        <AppSelect
          v-model="bankCategorySearchParams.status"
          :list="statusOptions"
          :select-props="{ placeholder: '状态', clearable: true }"
          @change="dataInfo.searchBankCategory()"
        />
      </div>

      <div v-loading="categoryLoading" class="category-list">
        <button
          v-for="item in bankCategoryList"
          :key="item.id"
          type="button"
          class="category-item"
          :class="{ 'is-active': String(item.id) === String(selectedBankCategoryId) }"
          @click="dataInfo.selectBankCategory(item)"
        >
          <div class="category-item-main">
            <strong>{{ item.categoryName }}</strong>
          </div>
          <div class="category-item-meta">
            <span>{{ item.bankCount ?? 0 }} 个题库</span>
          </div>
          <el-dropdown class="category-item-more" trigger="hover" @click.stop @command="dataInfo.handleBankCategoryCommand(item, $event)">
            <button type="button" class="more-button" @click.stop>...</button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="edit" v-permission="'question:bankCategory:update'">编辑</el-dropdown-item>
                <el-dropdown-item command="delete" v-permission="'question:bankCategory:delete'">删除</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </button>

        <el-empty v-if="!categoryLoading && !bankCategoryList.length" description="暂无题库分类" />
      </div>

      <AppPager
        v-model:page-index="bankCategoryPageInfo.pageNum"
        v-model:page-size="bankCategoryPageInfo.pageSize"
        is-small
        :total="bankCategoryTotal"
        :pagination-props="{ layout: 'prev,pager,next', small: true }"
        @change="dataInfo.getBankCategoryList()"
      />
    </section>

    <section class="bank-pane">
      <AppTableList>
        <AppListHeader>
          <el-row :gutter="16" style="width: 100%">
            <el-col :xs="24" :sm="8" :md="7" :lg="6">
              <AppInput
                v-model="searchParams.categoryName"
                placeholder="请输入题库名称"
                :icon-props="{ place: 'suffix', name: 'Search' }"
                :input-props="{ clearable: true }"
                @input="dataInfo.debounceSearch()"
              />
            </el-col>
            <el-col :xs="24" :sm="8" :md="6" :lg="5">
              <AppSelect v-model="searchParams.status" :list="statusOptions" :select-props="{ placeholder: '请选择状态', clearable: true }" @change="dataInfo.search()" />
            </el-col>
            <el-col :xs="24" :sm="8" :md="11" :lg="13">
              <div class="header-handle">
                <span class="current-category">当前分类：{{ selectedBankCategory?.categoryName || '未选择' }}</span>
                <AppButton :button-props="{ loading }" @click="dataInfo.getList()">刷新</AppButton>
                <AppButton :button-props="{ type: 'primary', disabled: !selectedBankCategoryId }" v-permission="'question:category:update'" @click="dataInfo.openCreate()">
                  新增题库
                </AppButton>
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
    </section>

    <QuestionBankCategoryFormDialog
      v-model="dataInfo.bankCategoryDialogVisible"
      :select-item="dataInfo.selectedBankCategoryRecord"
      @success="dataInfo.handleBankCategorySaved()"
    />
    <QuestionCategoryFormDialog
      v-model="dataInfo.dialogVisible"
      :select-item="dataInfo.selectedRecord"
      :default-bank-category-id="selectedBankCategoryId"
      @success="dataInfo.handleQuestionCategorySaved()"
    />
    <GrantQuestionCategoryDialog v-model="dataInfo.grantDialogVisible" />
  </main>
</template>

<script setup lang="ts" name="QuestionCategory">
import { reactive, toRefs } from 'vue';
import $utils from '@vue-scaffold/utils';
import { $apis } from '@/api/requests';
import type { QuestionBankCategoryRecord, QuestionCategoryRecord } from '@/types/domain';
import QuestionBankCategoryFormDialog from '@/views/question/components/QuestionBankCategoryFormDialog.vue';
import QuestionCategoryFormDialog from '@/views/question/components/QuestionCategoryFormDialog.vue';
import GrantQuestionCategoryDialog from '@/views/question/components/GrantQuestionCategoryDialog.vue';
import tableInfo from '@/views/question/tables/QuestionCategory';

const statusOptions = [
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 },
];

const dataInfo: any = reactive({
  bankCategoryPageInfo: { pageNum: 1, pageSize: 10 },
  bankCategorySearchParams: { categoryName: '', status: '' },
  bankCategoryList: [] as QuestionBankCategoryRecord[],
  bankCategoryTotal: 0,
  selectedBankCategoryId: '' as string | number,
  selectedBankCategory: null as QuestionBankCategoryRecord | null,
  pageInfo: { pageNum: 1, pageSize: 10 },
  searchParams: { categoryName: '', status: '' },
  list: [] as QuestionCategoryRecord[],
  total: 0,
  categoryLoading: false,
  loading: false,
  actionLoading: false,
  dialogVisible: false,
  grantDialogVisible: false,
  bankCategoryDialogVisible: false,
  selectedRecord: null as QuestionCategoryRecord | null,
  selectedBankCategoryRecord: null as QuestionBankCategoryRecord | null,
  get bankCategoryParams() {
    return { ...this.bankCategorySearchParams, ...this.bankCategoryPageInfo };
  },
  get params() {
    return {
      ...this.searchParams,
      ...this.pageInfo,
      bankCategoryId: this.selectedBankCategoryId || '',
    };
  },
  async initData() {
    await this.getBankCategoryList();
    await this.getList();
  },
  async getBankCategoryList() {
    this.categoryLoading = true;
    try {
      const data = await $apis.questionBankCategories.list(this.bankCategoryParams);
      this.bankCategoryTotal = data?.total ?? 0;
      this.bankCategoryList = data?.records ?? [];
      this.syncSelectedBankCategory();
    } finally {
      this.categoryLoading = false;
    }
  },
  syncSelectedBankCategory() {
    if (!this.bankCategoryList.length) {
      this.selectedBankCategoryId = '';
      this.selectedBankCategory = null;
      return;
    }

    const selected = this.bankCategoryList.find((item: QuestionBankCategoryRecord) => String(item.id) === String(this.selectedBankCategoryId));
    const nextSelected = selected || this.bankCategoryList[0];
    this.selectedBankCategoryId = nextSelected.id;
    this.selectedBankCategory = nextSelected;
  },
  async selectBankCategory(row: QuestionBankCategoryRecord) {
    this.selectedBankCategoryId = row.id;
    this.selectedBankCategory = row;
    this.search();
  },
  searchBankCategory() {
    this.bankCategoryPageInfo.pageNum = 1;
    this.getBankCategoryList().then(() => this.search());
  },
  debounceBankCategorySearch: $utils.Tool.debounce(function (this: any) {
    this.searchBankCategory();
  }, 300),
  async getList() {
    if (!this.selectedBankCategoryId) {
      this.total = 0;
      this.list = [];
      return;
    }
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
  openBankCategoryCreate() {
    this.selectedBankCategoryRecord = null;
    this.bankCategoryDialogVisible = true;
  },
  openBankCategoryEdit(row: QuestionBankCategoryRecord) {
    this.selectedBankCategoryRecord = row;
    this.bankCategoryDialogVisible = true;
  },
  async deleteBankCategory(row: QuestionBankCategoryRecord) {
    if (this.actionLoading) return;
    this.actionLoading = true;
    try {
      await $utils.Message.messageConfirm(`确认删除题库分类"${row.categoryName}"吗？`);
      await $apis.questionBankCategories.delete({ categoryId: Number(row.id) });
      $utils.Message.messageAlert({ message: '删除成功' });
      await this.getBankCategoryList();
      await this.getList();
    } finally {
      this.actionLoading = false;
    }
  },
  async handleBankCategoryCommand(row: QuestionBankCategoryRecord, command: string) {
    const actionMap: Record<string, () => void | Promise<void>> = {
      edit: () => dataInfo.openBankCategoryEdit(row),
      delete: () => dataInfo.deleteBankCategory(row),
    };
    await actionMap[command]?.();
  },
  async handleBankCategorySaved() {
    await this.getBankCategoryList();
    await this.getList();
  },
  openCreate() {
    if (!this.selectedBankCategoryId) {
      $utils.Message.messageAlert({ message: '请先选择题库分类', type: 'warning' });
      return;
    }
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
      await $utils.Message.messageConfirm(`确认删除题库"${row.categoryName}"吗？`);
      await $apis.questionCategories.delete({ categoryId: Number(row.id) });
      $utils.Message.messageAlert({ message: '删除成功' });
      await this.getList();
      await this.getBankCategoryList();
    } finally {
      this.actionLoading = false;
    }
  },
  async handleQuestionCategorySaved() {
    await this.getList();
    await this.getBankCategoryList();
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

const {
  bankCategoryPageInfo,
  bankCategorySearchParams,
  bankCategoryList,
  bankCategoryTotal,
  selectedBankCategoryId,
  selectedBankCategory,
  pageInfo,
  searchParams,
  list,
  total,
  categoryLoading,
  loading,
} = toRefs(dataInfo);

dataInfo.initData();
</script>

<style scoped lang="scss">
.QuestionCategory-root {
  flex: 1;
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr);
  gap: 16px;
  width: 100%;
  min-height: 0;
  overflow: hidden;
}

.category-pane,
.bank-pane {
  min-height: 0;
}

.category-pane {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 16px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 20px;
}

.pane-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.pane-header h2 {
  margin: 0;
  color: #111827;
  font-size: 18px;
  font-weight: 700;
  line-height: 1.35;
}

.pane-header span,
.category-item-meta {
  color: #64748b;
  font-size: 12px;
}

.pane-actions,
.header-handle,
.category-item-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.category-filters {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 112px;
  gap: 8px;
}

.category-list {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 10px;
  min-height: 0;
  overflow-y: auto;
  padding-right: 2px;
}

.category-item {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto auto;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 12px;
  text-align: left;
  cursor: pointer;
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  transition:
    border-color 0.18s ease,
    background 0.18s ease,
    box-shadow 0.18s ease;
}

.category-item:hover,
.category-item.is-active {
  background: #eef6ff;
  border-color: #409eff;
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.12);
}

.category-item-main {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.category-item-main strong {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.category-item-main strong {
  color: #111827;
  font-size: 14px;
}

.more-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  padding: 0;
  color: #64748b;
  font-size: 18px;
  line-height: 1;
  cursor: pointer;
  background: transparent;
  border: 0;
  border-radius: 6px;
}

.more-button:hover {
  color: #111827;
  background: rgba(15, 23, 42, 0.08);
}

.bank-pane {
  overflow: hidden;
}

.category-pane :deep(.AppPager-root) {
  justify-content: center;
  padding-bottom: 0;
}

.current-category {
  flex: 1;
  min-width: 120px;
  overflow: hidden;
  color: #475569;
  font-size: 13px;
  text-align: right;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.header-handle {
  justify-content: flex-end;
}

@media (max-width: 1200px) {
  .QuestionCategory-root {
    grid-template-columns: 1fr;
    overflow: auto;
  }

  .category-pane {
    max-height: 360px;
  }

  .current-category {
    width: 100%;
    text-align: left;
  }

  .header-handle {
    flex-wrap: wrap;
    justify-content: flex-start;
  }
}
</style>
