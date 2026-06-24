<template>
  <main class="Role-root">
    <AppTableList>
      <AppListHeader>
        <el-row :gutter="16" style="width: 100%">
          <el-col :xs="24" :sm="24" :md="8" :lg="6" :xl="5">
            <AppInput
              v-model="searchParams.keyword"
              placeholder="角色名称或编码"
              :icon-props="{ place: 'suffix', name: 'Search' }"
              :input-props="{ clearable: true }"
              @input="dataInfo.debounceSearch()"
            />
          </el-col>
          <el-col :xs="24" :sm="24" :md="16" :lg="18" :xl="19">
            <div class="header-handle">
              <AppButton :button-props="{ loading }" @click="dataInfo.refreshPageData()">
                刷新
              </AppButton>
              <AppButton
                :button-props="{ type: 'primary' }"
                v-permission="'system:role:update'"
                @click="dataInfo.openCreate()"
              >
                新增角色
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

    <RoleFormDialog
      v-model="dataInfo.dialogVisible"
      :select-item="dataInfo.selectedRecord"
      @success="dataInfo.getList()"
    />

    <GrantRoleMenusDialog
      v-model="dataInfo.grantDialogVisible"
      :select-item="dataInfo.selectedGrantRole"
      :menus="grantMenus"
      :checked-menu-ids="checkedMenuIds"
      @success="dataInfo.handleGrantSuccess"
    />

    <GrantRoleUsersDialog
      v-model="dataInfo.grantUsersDialogVisible"
      :select-item="dataInfo.selectedGrantRoleForUsers"
      @success="dataInfo.handleGrantUsersSuccess"
    />
  </main>
</template>

<script setup lang="ts" name="Role">
import { reactive, toRefs } from 'vue';
import $utils from '@vue-scaffold/utils';
import RoleFormDialog from '@/views/system/components/RoleFormDialog.vue';
import GrantRoleMenusDialog, { type GrantMenuTreeNode } from '@/views/system/components/GrantRoleMenusDialog.vue';
import GrantRoleUsersDialog from '@/views/system/components/GrantRoleUsersDialog.vue';
import tableInfo from '@/views/system/tables/Role';
import { $apis } from '@/api/requests';
import type { RoleRecord } from '@/types/domain';


type RolePageItem = {
  id: number | string;
  code?: string;
  name?: string;
  status?: number;
  remark?: string;
  createTime?: string;
  updateTime?: string;
};

type MenuTreeItem = {
  id: number | string;
  menuName?: string;
  path?: string;
  children?: MenuTreeItem[];
};

type RoleGrantInfo = {
  menuIds?: number[];
};

const dataInfo = reactive({
  pageInfo: {
    pageNum: 1,
    pageSize: 10,
  },
  searchParams: {
    keyword: '',
  },
  dialogVisible: false,
  grantDialogVisible: false,
  grantUsersDialogVisible: false,
  selectedRecord: null as RoleRecord | null,
  selectedGrantRole: null as RoleRecord | null,
  selectedGrantRoleForUsers: null as RoleRecord | null,
  grantMenus: [] as GrantMenuTreeNode[],
  checkedMenuIds: [] as number[],
  list: [] as RoleRecord[],
  total: 0,
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
      const result = await $apis.roles.list(this.params);
      this.list = Array.isArray(result?.records)
        ? result.records.map((item: RolePageItem) => mapRole(item))
        : [];
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
    this.search();
  }, 300),
  openCreate() {
    this.selectedRecord = null;
    this.dialogVisible = true;
  },
  openEdit(row: RoleRecord) {
    this.selectedRecord = row;
    this.dialogVisible = true;
  },
  async openGrantMenus(row: RoleRecord) {
    this.actionLoading = true;
    try {
      const [menuResult, grantInfoResult] = await Promise.all([
        $apis.menus.tree({}),
        $apis.roles.grantInfo({ id: Number(row.id) }),
      ]);
      this.grantMenus = Array.isArray(menuResult)
        ? menuResult.map((item: MenuTreeItem) => mapMenuTree(item))
        : [];
      this.checkedMenuIds = Array.isArray((grantInfoResult as RoleGrantInfo | undefined)?.menuIds)
        ? (((grantInfoResult as RoleGrantInfo | undefined)?.menuIds) ?? [])
        : [];
      this.selectedGrantRole = row;
      this.grantDialogVisible = true;
    } finally {
      this.actionLoading = false;
    }
  },
  handleGrantSuccess() {
    this.grantDialogVisible = false;
  },
  async openGrantUsers(row: RoleRecord) {
    this.selectedGrantRoleForUsers = row;
    await Promise.resolve();
    this.grantUsersDialogVisible = true;
  },
  handleGrantUsersSuccess() {
    this.grantUsersDialogVisible = false;
  },
  async deleteRole(row: RoleRecord) {
    if (this.actionLoading) {
      return;
    }

    this.actionLoading = true;
    try {
      await $utils.Message.messageConfirm(`确认删除角色“${row.roleName}”吗？`);
      await $apis.roles.delete({
        roleId: Number(row.id),
      });
      $utils.Message.messageAlert({ message: '角色删除成功' });
      await this.getList();
    } finally {
      this.actionLoading = false;
    }
  },
  async handleAction(row: RoleRecord, action: Record<string, any>) {
    if (dataInfo.actionLoading) {
      return;
    }

    const actionMap: Record<string, () => void | Promise<void>> = {
      grantMenus: () => dataInfo.openGrantMenus(row),
      grantUsers: () => dataInfo.openGrantUsers(row),
      edit: () => dataInfo.openEdit(row),
      delete: () => dataInfo.deleteRole(row),
    };

    await actionMap[action.key]?.();
  },
  async init() {
    await this.getList();
  },
});

function mapRole(item: RolePageItem): RoleRecord {
  return {
    id: String(item.id),
    roleCode: item.code ?? '',
    roleName: item.name ?? '',
    userNum: 0,
    userGroupNum: 0,
    systemDefault: ['systemAdmin', 'ADMIN', 'SUPER_ADMIN'].includes(item.code ?? ''),
    remark: item.remark ?? '',
    createdAt: item.createTime ?? '',
    updatedAt: item.updateTime ?? '',
  };
}

function mapMenuTree(item: MenuTreeItem): GrantMenuTreeNode {
  return {
    id: String(item.id),
    label: item.path ? `${item.menuName ?? ''} (${item.path})` : (item.menuName ?? ''),
    children: Array.isArray(item.children) ? item.children.map(mapMenuTree) : [],
  };
}

const { pageInfo, searchParams, grantMenus, checkedMenuIds, list, total, loading } = toRefs(dataInfo);

dataInfo.init();
</script>

<style scoped lang="scss">
.Role-root {
  height: 100%;
}

.header-handle {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}
</style>
