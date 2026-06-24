<template>
  <main class="QuestionRecord-root">
    <AppTableList>
      <AppListHeader>
        <el-row :gutter="16" style="width: 100%">
          <el-col :xs="12" :sm="8" :md="5">
            <AppSelect v-model="searchParams.categoryId" :list="categoryOptions" :select-props="{ placeholder: '请选择题目分类' }" @change="dataInfo.search()" />
          </el-col>
          <el-col :xs="12" :sm="8" :md="5">
            <AppInput v-model="searchParams.userId" placeholder="请输入用户ID" :icon-props="{ place: 'suffix', name: 'Search' }" @input="dataInfo.debounceSearch()" />
          </el-col>
          <el-col :xs="24" :sm="8" :md="14">
            <div class="header-handle">
              <AppButton :button-props="{ loading }" @click="dataInfo.getList()">刷新</AppButton>
            </div>
          </el-col>
        </el-row>
      </AppListHeader>
      <AppTable :table-props="{ data: list }" :table-info="tableInfo" :page-info="pageInfo" :loading="loading" @handle-click="dataInfo.handleAction" />
      <AppPager v-model:page-index="pageInfo.pageNum" v-model:page-size="pageInfo.pageSize" :total="total" @change="dataInfo.getList()" />
    </AppTableList>
    <el-dialog v-model="dataInfo.detailVisible" title="答题详情" width="880px">
      <div v-loading="dataInfo.detailLoading">
        <div class="score-line">
          <span>得分：{{ detail?.userScore ?? 0 }} / {{ detail?.totalScore ?? 0 }}</span>
          <span>正确：{{ detail?.correctCount ?? 0 }} / {{ detail?.questionCount ?? 0 }}</span>
        </div>
        <el-table :data="detail?.details ?? []" border>
          <el-table-column label="题干" prop="title" min-width="220" />
          <el-table-column label="用户答案" prop="userAnswer" min-width="120" />
          <el-table-column label="正确答案" prop="correctAnswer" min-width="120" />
          <el-table-column label="得分" prop="userScore" width="90" />
          <el-table-column label="结果" width="90">
            <template #default="{ row }">
              <el-tag :type="row.correctFlag ? 'success' : 'danger'">{{ row.correctFlag ? '正确' : '错误' }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </main>
</template>

<script setup lang="ts" name="QuestionRecord">
import { reactive, toRefs } from 'vue';
import $utils from '@vue-scaffold/utils';
import { $apis } from '@/api/requests';
import type { QuestionAnswerRecord } from '@/types/domain';
import tableInfo from '@/views/question/tables/QuestionRecord';

const dataInfo: any = reactive({
  pageInfo: { pageNum: 1, pageSize: 10 },
  searchParams: { categoryId: '', userId: '' },
  categories: [] as Array<Record<string, any>>,
  list: [] as QuestionAnswerRecord[],
  detail: null as QuestionAnswerRecord | null,
  total: 0,
  loading: false,
  detailLoading: false,
  detailVisible: false,
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
      const data = await $apis.questionAnswerRecords.list(this.params);
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
  async openDetail(row: QuestionAnswerRecord) {
    this.detailVisible = true;
    this.detailLoading = true;
    try {
      this.detail = await $apis.questionAnswerRecords.detail({ id: Number(row.id) });
    } finally {
      this.detailLoading = false;
    }
  },
  async handleAction(row: QuestionAnswerRecord, action: Record<string, any>) {
    if (action.key === 'detail') {
      await this.openDetail(row);
    }
  },
});

const { pageInfo, searchParams, list, total, loading, categoryOptions, detail } = toRefs(dataInfo);
dataInfo.initData();
</script>

<style scoped lang="scss">
.QuestionRecord-root {
  height: 100%;
}

.header-handle {
  display: flex;
  justify-content: flex-end;
}

.score-line {
  display: flex;
  gap: 24px;
  margin-bottom: 12px;
  color: #303133;
  font-weight: 600;
}
</style>

