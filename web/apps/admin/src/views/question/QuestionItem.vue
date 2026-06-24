<template>
  <main class="QuestionItem-root">
    <section class="bank-pane">
      <div class="pane-header">
        <div>
          <h2>题库</h2>
          <span>{{ categoryTotal }} 个题库</span>
        </div>
        <AppButton :button-props="{ loading: categoryLoading }" @click="dataInfo.getCategoryList()">刷新</AppButton>
      </div>

      <div class="bank-filters">
        <AppSelect
          v-model="categorySearchParams.bankCategoryId"
          :list="bankCategoryOptions"
          :select-props="{ placeholder: '题库分类', clearable: true }"
          @change="dataInfo.searchCategory()"
        />
        <AppInput
          v-model="categorySearchParams.categoryName"
          placeholder="搜索题库"
          :icon-props="{ place: 'suffix', name: 'Search' }"
          :input-props="{ clearable: true }"
          @input="dataInfo.searchCategory()"
        />
      </div>

      <div v-loading="categoryLoading" class="bank-list">
        <button
          v-for="item in categoryList"
          :key="item.id"
          type="button"
          class="bank-item"
          :class="{ 'is-active': String(item.id) === String(selectedCategoryId) }"
          @click="dataInfo.selectCategory(item)"
        >
          <strong>{{ item.categoryName }}</strong>
          <span>{{ item.questionCount ?? 0 }} 道题目</span>
        </button>

        <el-empty v-if="!categoryLoading && !categoryList.length" description="暂无题库" />
      </div>

      <AppPager
        v-model:page-index="categoryPageInfo.pageNum"
        v-model:page-size="categoryPageInfo.pageSize"
        is-small
        :total="categoryTotal"
        :pagination-props="{ layout: 'total,prev,pager,next', small: true }"
        @change="dataInfo.getCategoryList()"
      />
    </section>

    <section class="question-pane">
      <AppTableList>
        <AppListHeader>
          <el-row :gutter="16" style="width: 100%">
            <el-col :xs="24" :sm="8" :md="6" :lg="5">
              <AppSelect v-model="searchParams.questionType" :list="questionTypeOptions" :select-props="{ placeholder: '请选择题型', clearable: true }" @change="dataInfo.search()" />
            </el-col>
            <el-col :xs="24" :sm="8" :md="7" :lg="6">
              <AppInput
                v-model="searchParams.title"
                placeholder="请输入题干"
                :icon-props="{ place: 'suffix', name: 'Search' }"
                :input-props="{ clearable: true }"
                @input="dataInfo.debounceSearch()"
              />
            </el-col>
            <el-col :xs="24" :sm="8" :md="11" :lg="13">
              <div class="header-handle">
                <span class="current-bank">当前题库：{{ selectedCategory?.categoryName || '未选择' }}</span>
                <AppButton :button-props="{ loading }" @click="dataInfo.getList()">刷新</AppButton>
                <AppButton :button-props="{ type: 'primary', disabled: !selectedCategoryId }" v-permission="'question:item:update'" @click="dataInfo.openCreate()">
                  新增题目
                </AppButton>
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
    </section>

    <QuestionFormDialog
      v-model="dataInfo.dialogVisible"
      :select-item="dataInfo.selectedRecord"
      :default-category-id="selectedCategoryId"
      @success="dataInfo.handleQuestionSaved()"
    />
  </main>
</template>

<script setup lang="ts" name="QuestionItem">
import { reactive, toRefs } from 'vue';
import $utils from '@vue-scaffold/utils';
import { $apis } from '@/api/requests';
import type { QuestionBankCategoryRecord, QuestionCategoryRecord, QuestionRecord } from '@/types/domain';
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
  categoryPageInfo: { pageNum: 1, pageSize: 10 },
  categorySearchParams: { bankCategoryId: '', categoryName: '' },
  bankCategories: [] as QuestionBankCategoryRecord[],
  categoryList: [] as QuestionCategoryRecord[],
  categoryTotal: 0,
  selectedCategoryId: '' as string | number,
  selectedCategory: null as QuestionCategoryRecord | null,
  pageInfo: { pageNum: 1, pageSize: 10 },
  searchParams: { questionType: '', title: '' },
  list: [] as QuestionRecord[],
  total: 0,
  categoryLoading: false,
  loading: false,
  actionLoading: false,
  dialogVisible: false,
  selectedRecord: null as QuestionRecord | null,
  get categoryParams() {
    return { ...this.categorySearchParams, ...this.categoryPageInfo };
  },
  get bankCategoryOptions() {
    return this.bankCategories.map((item: QuestionBankCategoryRecord) => ({ label: item.categoryName, value: Number(item.id) }));
  },
  get params() {
    return {
      ...this.searchParams,
      ...this.pageInfo,
      categoryId: this.selectedCategoryId || '',
    };
  },
  async initData() {
    this.bankCategories = await $apis.questionBankCategories.listAll();
    await this.getCategoryList();
    await this.getList();
  },
  async getCategoryList() {
    this.categoryLoading = true;
    try {
      const data = await $apis.questionCategories.list(this.categoryParams);
      this.categoryTotal = data?.total ?? 0;
      this.categoryList = data?.records ?? [];
      this.syncSelectedCategory();
    } finally {
      this.categoryLoading = false;
    }
  },
  syncSelectedCategory() {
    if (!this.categoryList.length) {
      this.selectedCategoryId = '';
      this.selectedCategory = null;
      return;
    }

    const selected = this.categoryList.find((item: QuestionCategoryRecord) => String(item.id) === String(this.selectedCategoryId));
    const nextSelected = selected || this.categoryList[0];
    this.selectedCategoryId = nextSelected.id;
    this.selectedCategory = nextSelected;
  },
  selectCategory(row: QuestionCategoryRecord) {
    this.selectedCategoryId = row.id;
    this.selectedCategory = row;
    this.search();
  },
  searchCategory() {
    this.categoryPageInfo.pageNum = 1;
    this.getCategoryList().then(() => this.search());
  },
  async getList() {
    if (!this.selectedCategoryId) {
      this.total = 0;
      this.list = [];
      return;
    }

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
    if (!this.selectedCategoryId) {
      $utils.Message.messageAlert({ message: '请先选择题库', type: 'warning' });
      return;
    }
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
      await this.getCategoryList();
    } finally {
      this.actionLoading = false;
    }
  },
  async handleQuestionSaved() {
    await this.getList();
    await this.getCategoryList();
  },
  async handleAction(row: QuestionRecord, action: Record<string, any>) {
    const actionMap: Record<string, () => void | Promise<void>> = {
      edit: () => dataInfo.openEdit(row),
      delete: () => dataInfo.deleteQuestion(row),
    };
    await actionMap[action.key]?.();
  },
});

const {
  categoryPageInfo,
  categorySearchParams,
  bankCategoryOptions,
  categoryList,
  categoryTotal,
  selectedCategoryId,
  selectedCategory,
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
.QuestionItem-root {
  flex: 1;
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr);
  gap: 16px;
  width: 100%;
  min-height: 0;
  overflow: hidden;
}

.bank-pane,
.question-pane {
  min-height: 0;
}

.bank-pane {
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
.bank-item span {
  color: #64748b;
  font-size: 12px;
}

.bank-list {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 10px;
  min-height: 0;
  overflow-y: auto;
  padding-right: 2px;
}

.bank-filters {
  display: grid;
  gap: 8px;
}

.bank-item {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
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

.bank-item:hover,
.bank-item.is-active {
  background: #eef6ff;
  border-color: #409eff;
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.12);
}

.bank-item strong {
  min-width: 0;
  overflow: hidden;
  color: #111827;
  font-size: 14px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.question-pane {
  overflow: hidden;
}

.bank-pane :deep(.AppPager-root) {
  justify-content: center;
  padding-bottom: 0;
  flex-wrap: wrap;
  row-gap: 6px;
}

.header-handle {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: flex-end;
}

.current-bank {
  flex: 1;
  min-width: 120px;
  overflow: hidden;
  color: #475569;
  font-size: 13px;
  text-align: right;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@media (max-width: 1200px) {
  .QuestionItem-root {
    grid-template-columns: 1fr;
    overflow: auto;
  }

  .bank-pane {
    max-height: 360px;
  }

  .header-handle {
    flex-wrap: wrap;
    justify-content: flex-start;
  }

  .current-bank {
    width: 100%;
    text-align: left;
  }
}
</style>
