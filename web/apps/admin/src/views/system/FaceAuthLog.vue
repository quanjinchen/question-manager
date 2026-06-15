<template>
  <main class="FaceAuthLog-root">
    <AppTableList>
      <AppListHeader>
        <div class="header-layout">
          <div class="search-grid">
            <AppSelect
              v-model="searchParams.authApiType"
              :list="authApiTypeOptions"
              :select-props="{ placeholder: '认证接口' }"
              @change="dataInfo.search()"
            />
            <AppInput
              v-model="searchParams.ip"
              placeholder="请求 IP"
              :icon-props="{ place: 'suffix', name: 'Search' }"
              @input="dataInfo.debounceSearch()"
            />
            <AppSelect
              v-model="searchParams.status"
              :list="statusOptions"
              :select-props="{ placeholder: '认证状态' }"
              @change="dataInfo.search()"
            />
            <AppInput
              v-model="searchParams.appName"
              placeholder="应用名称"
              :icon-props="{ place: 'suffix', name: 'Search' }"
              @input="dataInfo.debounceSearch()"
            />
            <AppInput
              v-model="searchParams.authFullName"
              placeholder="认证人姓名"
              :icon-props="{ place: 'suffix', name: 'Search' }"
              @input="dataInfo.debounceSearch()"
            />
          </div>
          <div class="header-handle">
            <AppButton :button-props="{ loading }" @click="dataInfo.refreshPageData()">
              刷新
            </AppButton>
          </div>
        </div>
      </AppListHeader>

      <AppTable
        :table-props="{ data: list }"
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

<script setup lang="ts" name="FaceAuthLog">
import { reactive, toRefs } from 'vue';
import $utils from '@vue-scaffold/utils';
import { dictStore } from '@vue-scaffold/constants';
import { $apis } from '@/api/requests';
import type { FaceAuthLogRecord } from '@/types/domain';
import tableInfo from '@/views/system/tables/FaceAuthLog';


const authApiTypeOptions = [
  { label: '全部接口', value: '' },
  ...dictStore.faceAuthApiTypeList,
];

const statusOptions = [
  { label: '全部状态', value: '' },
  ...dictStore.faceStatusList
];

const dataInfo = reactive({
  pageInfo: {
    pageNum: 1,
    pageSize: 10,
  },
  searchParams: {
    authApiType: '',
    ip: '',
    status: '',
    appName: '',
    authFullName: '',
  },
  list: [] as FaceAuthLogRecord[],
  total: 0,
  loading: false,
  get params() {
    return {
      ...this.pageInfo,
      ...this.searchParams
    };
  },
  async getList() {
    this.loading = true;
    try {
      const result = await $apis.faceAuthLogs.page(this.params);
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
  search() {
    this.pageInfo.pageNum = 1;
    this.getList();
  },
  debounceSearch: $utils.Tool.debounce(function (this: any) {
    dataInfo.search();
  }, 300),
  async init() {
    await this.getList();
  },
});

const { pageInfo, searchParams, list, total, loading } = toRefs(dataInfo);

dataInfo.init();
</script>

<style scoped lang="scss">
.FaceAuthLog-root {
  height: 100%;
}

.header-layout {
  display: flex;
  gap: 16px;
  align-items: flex-start;
  width: 100%;
}

.search-grid {
  display: flex;
  flex: 1 1 auto;
  flex-wrap: wrap;
  min-width: 0;
  margin: -8px;
}

.search-grid :deep(> *) {
  flex: 1 1 180px;
  min-width: 180px;
  margin: 8px;
}

.header-handle {
  display: flex;
  flex: 0 0 auto;
  gap: 8px;
  justify-content: flex-end;
  white-space: nowrap;
}

@media (max-width: 991px) {
  .header-layout {
    flex-direction: column;
  }

  .header-handle {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
