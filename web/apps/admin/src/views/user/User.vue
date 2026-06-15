<!-- 用户管理 -->
<template>
  <main class="User-root">
    <AppTableList>
      <AppListHeader>
        <el-row :gutter="16" style="width: 100%">
          <el-col :xs="12" :sm="12" :md="6" :lg="5" :xl="4">
            <AppInput
              v-model="searchParams.username"
              placeholder="用户名"
              :icon-props="{ place: 'suffix', name: 'Search' }"
              @input="dataInfo.debounceSearch()"
            />
          </el-col>
          <el-col :xs="12" :sm="12" :md="6" :lg="5" :xl="4">
            <AppInput
              v-model="searchParams.fullName"
              placeholder="姓名"
              :icon-props="{ place: 'suffix', name: 'Search' }"
              @input="dataInfo.debounceSearch()"
            />
          </el-col>
          <el-col :xs="12" :sm="12" :md="6" :lg="5" :xl="4">
            <AppInput
              v-model="searchParams.phone"
              placeholder="手机号"
              :icon-props="{ place: 'suffix', name: 'Search' }"
              @input="dataInfo.debounceSearch()"
            />
          </el-col>
          <el-col :xs="12" :sm="12" :md="6" :lg="5" :xl="4">
            <AppInput
              v-model="searchParams.email"
              placeholder="邮箱"
              :icon-props="{ place: 'suffix', name: 'Search' }"
              @input="dataInfo.debounceSearch()"
            />
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="4" :xl="8">
            <div class="header-handle">
              <AppButton
                :button-props="{ loading }"
                @click="dataInfo.refreshPageData()"
                >刷新</AppButton
              >
              <AppButton
                :button-props="{ type: 'primary' }"
                v-permission="'system:user:add'"
                @click="dataInfo.openCreate()"
                >新增用户</AppButton
              >
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
      >
        <template #faceRegistered="{ row }">
          <el-tag
            :type="dataInfo.hasFaceFeature(row) ? 'success' : 'info'"
          >
            {{ dataInfo.hasFaceFeature(row) ? '已注册' : '未注册' }}
          </el-tag>
        </template>
      </AppTable>

      <AppPager
        v-model:page-index="pageInfo.pageNum"
        v-model:page-size="pageInfo.pageSize"
        :total="total"
        @change="dataInfo.getList()"
      />
    </AppTableList>

    <UserFormDialog
      v-model="dataInfo.dialogVisible"
      :selectItem="dataInfo.selectedRecord"
      @success="dataInfo.getList()"
    />
  </main>
</template>

<script setup lang="ts" name="User">
import { reactive, toRefs } from "vue";
import $utils from '@vue-scaffold/utils';
import UserFormDialog from "@/views/user/components/UserFormDialog.vue";
import { $apis } from "@/api/requests";
import type { UserRecord } from "@/types/domain";
import tableInfo from "@/views/user/tables/User";


// 数据信息
const dataInfo: any = reactive({
  pageInfo: { pageNum: 1, pageSize: 10 },
  searchParams: {
    username: "",
    fullName: "",
    phone: "",
    email: "",
  },
  dialogVisible: false,
  selectedRecord: null as UserRecord | null,
  total: 0,
  list: [] as UserRecord[],
  loading: false,
  actionLoading: false,
  // 请求参数
  get params() {
    return {
      ...this.searchParams,
      ...this.pageInfo,
    };
  },
  // 获取用户列表
  async getList() {
    this.loading = true;
    try {
      const data = await $apis.users.list(this.params);
      this.total = data?.total;
      this.list = data.records;
    } finally {
      this.loading = false;
    }
  },

  // 刷新页面数据
  async refreshPageData() {
    this.loading = true;
    try {
      await this.getList();
    } finally {
      this.loading = false;
    }
  },

  // 搜索
  search() {
    this.pageInfo.pageNum = 1;
    this.getList();
  },

  // 带防抖搜索
  debounceSearch: $utils.Tool.debounce(function (this: any) {
    this.search();
  }, 300),

  // 打开新增对话框
  openCreate() {
    this.selectedRecord = null;
    this.dialogVisible = true;
  },

  // 打开编辑对话框
  openEdit(row: UserRecord) {
    this.selectedRecord = row;
    this.dialogVisible = true;
  },

  // 重置密码
  async resetPassword(row: UserRecord) {
    this.actionLoading = true;
    try {
      await $utils.Message.messageConfirm(
        `确认重置用户"${row.fullName || row.username}"的密码吗？`,
      );
      await $apis.users.resetPassword({
        userId: Number(row.id),
      });
      $utils.Message.messageAlert({ message: "密码重置成功" });
      await this.getList();
    } finally {
      this.actionLoading = false;
    }
  },

  // 删除用户
  async deleteUser(row: UserRecord) {
    this.actionLoading = true;
    try {
      await $utils.Message.messageConfirm(`确认删除用户"${row.fullName || row.username}"吗？`);
      await $apis.users.delete({
        userId: Number(row.id),
      });
      $utils.Message.messageAlert({ message: "用户删除成功" });
      await this.getList();
    } finally {
      this.actionLoading = false;
    }
  },

  // 处理操作（分发）
  async handleAction(row: UserRecord, action: Record<string, any>) {
    if (dataInfo.actionLoading) {
      return;
    }

    const actionMap: Record<string, () => void | Promise<void>> = {
      edit: () => dataInfo.openEdit(row),
      resetPassword: () => dataInfo.resetPassword(row),
      delete: () => dataInfo.deleteUser(row),
    };

    const handler = actionMap[action.key];
    await handler?.();
  },

  hasFaceFeature(row: UserRecord) {
    // 兼容不同接口版本的人脸特征字段，任一字段有值即视为已注册人脸。
    return Boolean(
      row.faceFeature
        || row.faceFeatureValue
        || row.faceFeatureId
        || row.faceFileId,
    );
  },

  // 初始化
  async init() {
    await this.refreshPageData();
  },
});

const { pageInfo, loading, total, list, searchParams } = toRefs(dataInfo);

// 初始化
dataInfo.init();

// 暴露
defineExpose({ dataInfo });
</script>

<style scoped lang="scss">
.User-root {
  height: 100%;
}

.header-handle {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}
</style>
