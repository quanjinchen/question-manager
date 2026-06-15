<template>
  <main class="OperationLog-root">
    <AppTableList>
      <AppListHeader>
        <el-row :gutter="16" style="width: 100%">
          <el-col :xs="24" :sm="24" :md="10" :lg="8" :xl="6">
            <AppInput
              v-model="searchParams.keyword"
              placeholder="模块、动作、操作人"
              :icon-props="{ place: 'suffix', name: 'Search' }"
            />
          </el-col>
          <el-col :xs="24" :sm="24" :md="14" :lg="16" :xl="18">
            <div class="header-handle">
              <AppButton :button-props="{ loading }" @click="dataInfo.refreshPageData()">
                刷新
              </AppButton>
            </div>
          </el-col>
        </el-row>
      </AppListHeader>

      <AppTable
        :table-props="{ data: filteredList }"
        :table-info="tableInfo"
        :page-info="pageInfo"
        :loading="loading"
      />

      <AppPager
        v-model:page-index="pageInfo.pageNum"
        v-model:page-size="pageInfo.pageSize"
        :total="total"
        @change="dataInfo.getList()"
      />
    </AppTableList>
  </main>
</template>

<script setup lang="ts" name="OperationLog">
import { computed, reactive, toRefs } from 'vue';
import { $apis } from '@/api/requests';
import tableInfo from '@/views/system/tables/OperationLog';

const dataInfo = reactive({
  pageInfo: {
    pageNum: 1,
    pageSize: 10,
  },
  searchParams: {
    keyword: '',
  },
  list: [] as Record<string, any>[],
  total: 0,
  loading: false,
  get params() {
    return {
      pageNum: this.pageInfo.pageNum,
      pageSize: this.pageInfo.pageSize,
    };
  },
  async getList() {
    this.loading = true;
    try {
      const result = await $apis.operationLogs.page(this.params);
      this.list = Array.isArray(result?.records) ? result.records : [];
      this.total = Number(result?.total ?? 0);
      this.pageInfo.pageNum = Number(result?.pageNum ?? this.pageInfo.pageNum);
      this.pageInfo.pageSize = Number(result?.pageSize ?? this.pageInfo.pageSize);
    } finally {
      this.loading = false;
    }
  },
  async refreshPageData() {
    this.loading = true;
    try {
      await this.getList();
    } finally {
      this.loading = false;
    }
  },
  async init() {
    await this.getList();
  },
});

const filteredList = computed(() => {
  const keyword = dataInfo.searchParams.keyword.trim().toLowerCase();
  if (!keyword) {
    return dataInfo.list;
  }

  return dataInfo.list.filter(item => {
    const moduleName = String(item.moduleName ?? '').toLowerCase();
    const actionName = String(item.actionName ?? '').toLowerCase();
    const operatorName = String(item.operatorName ?? '').toLowerCase();
    return (
      moduleName.includes(keyword) ||
      actionName.includes(keyword) ||
      operatorName.includes(keyword)
    );
  });
});

const { pageInfo, searchParams, total, loading } = toRefs(dataInfo);

dataInfo.init();
</script>

<style scoped lang="scss">
.OperationLog-root {
  height: 100%;
}

.header-handle {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}
</style>
