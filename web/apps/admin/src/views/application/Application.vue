<template>
  <main class="Application-root">
    <AppTableList>
      <AppListHeader>
        <el-row :gutter="16" style="width: 100%">
          <el-col :xs="24" :sm="24" :md="8" :lg="6" :xl="5">
            <AppInput
              v-model="searchParams.keyword"
              placeholder="请输入应用名称、应用编码或Client ID"
              :icon-props="{ place: 'suffix', name: 'Search' }"
              @input="dataInfo.debounceSearch()"
            />
          </el-col>
          <el-col :xs="24" :sm="24" :md="16" :lg="18" :xl="19">
            <div class="header-handle">
              <AppButton @click="dataInfo.downloadDoc()">
                下载接口文档
              </AppButton>
              <AppButton :button-props="{ loading }" @click="dataInfo.refreshPageData()">
                刷新
              </AppButton>
              <AppButton
                :button-props="{ type: 'primary' }"
                v-permission="'system:app:update'"
                @click="dataInfo.openCreate()"
              >
                新增应用
              </AppButton>
            </div>
          </el-col>
        </el-row>
      </AppListHeader>

      <AppTable
        :table-props="{ data: list }"
        :table-info="tableInfo"
        :page-info="pageInfo"
        :loading="loading"
        @handle-click="dataInfo.handleAction"
      />

      <AppPager
        v-model:page-index="pageInfo.pageNum"
        v-model:page-size="pageInfo.pageSize"
        :total="total"
        @change="dataInfo.getList()"
      />
    </AppTableList>

    <AppFormDialog
      v-model="dataInfo.dialogVisible"
      :select-item="dataInfo.selectedRecord"
      @success="dataInfo.getList()"
    />
  </main>
</template>

<script setup lang="ts" name="Application">
import { reactive, toRefs } from 'vue';
import $utils from '@vue-scaffold/utils';
import { $apis } from '@/api/requests';
import type { AppRecord } from '@/types/domain';
import AppFormDialog from '@/views/application/components/AppFormDialog.vue';
import tableInfo from '@/views/application/tables/App';


const dataInfo = reactive({
  pageInfo: {
    pageNum: 1,
    pageSize: 10,
  },
  searchParams: {
    keyword: '',
  },
  dialogVisible: false,
  selectedRecord: null as AppRecord | null,
  total: 0,
  list: [] as AppRecord[],
  loading: false,
  actionLoading: false,
  get params() {
    return {
      ...this.pageInfo,
      keyword: this.searchParams.keyword,
    };
  },
  async getList() {
    this.loading = true;
    try {
      const result = await $apis.apps.list(this.params);
      this.list = result.records;
      this.total = Number(result?.total ?? 0);
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
  openCreate() {
    this.selectedRecord = null;
    this.dialogVisible = true;
  },
  downloadDoc() {
    window.open('/file/%E5%B0%8F%E5%9E%8B%E8%AE%A4%E8%AF%81%E5%B9%B3%E5%8F%B0%E6%8E%A5%E5%8F%A3%E6%96%87%E6%A1%A3.docx', '_blank');
  },
  openEdit(row: AppRecord) {
    this.selectedRecord = row;
    this.dialogVisible = true;
  },
  async deleteApp(row: AppRecord) {
    if (this.actionLoading) {
      return;
    }
    this.actionLoading = true;
    try {
      await $utils.Message.messageConfirm(`确认删除应用“${row.appName}”吗？`);
      await $apis.apps.delete({
        appId: Number(row.id),
      });
      $utils.Message.messageAlert({ message: '应用删除成功' });
      await this.getList();
    } finally {
      this.actionLoading = false;
    }
  },
  async handleAction(row: AppRecord, action: Record<string, any>) {
    if (dataInfo.actionLoading) {
      return;
    }

    const actionMap: Record<string, () => void | Promise<void>> = {
      edit: () => dataInfo.openEdit(row),
      delete: () => dataInfo.deleteApp(row),
    };

    await actionMap[action.key]?.();
  },
  async init() {
    await this.getList();
  },
});


const { pageInfo, searchParams, list, total, loading } = toRefs(dataInfo);

dataInfo.init();
</script>

<style scoped lang="scss">
.Application-root {
  height: 100%;
}

.header-handle {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}
</style>
