<template>
  <main class="Organization-root">
    <AppTableList>
      <AppListHeader>
        <el-row :gutter="16" style="width: 100%">
          <el-col :xs="12" :sm="12" :md="6" :lg="5" :xl="4">
            <AppInput
              v-model="searchParams.orgName"
              placeholder="组织名称"
              :icon-props="{ place: 'suffix', name: 'Search' }"
              @input="dataInfo.debounceSearch()"
            />
          </el-col>
          <el-col :xs="12" :sm="12" :md="6" :lg="5" :xl="4">
            <AppInput
              v-model="searchParams.shortName"
              placeholder="组织简称"
              :icon-props="{ place: 'suffix', name: 'Search' }"
              @input="dataInfo.debounceSearch()"
            />
          </el-col>
          <el-col :xs="24" :sm="24" :md="12" :lg="14" :xl="16">
            <div class="header-handle">
              <AppButton :button-props="{ loading }" @click="dataInfo.refreshPageData()">
                刷新
              </AppButton>
              <AppButton
                :button-props="{ type: 'primary' }"
                v-permission="'system:org:update'"
                @click="dataInfo.openCreate()"
              >
                新增组织
              </AppButton>
            </div>
          </el-col>
        </el-row>
      </AppListHeader>

      <AppTable
        :table-props="{
          data: filteredOrganizations,
          rowKey: 'id',
          treeProps: { children: 'children' },
          defaultExpandAll: true
        }"
        :table-info="tableInfo"
        :loading="loading"
        @handle-click="dataInfo.handleAction"
      />
    </AppTableList>

    <OrganizationFormDialog
      v-model="dataInfo.dialogVisible"
      :select-item="dataInfo.selectedRecord"
      :organizations="organizations"
      @success="dataInfo.getList()"
    />

    <GrantOrgUsersDialog
      v-model="dataInfo.grantDialogVisible"
      :organization="dataInfo.selectedGrantOrg"
      :users="userOptions"
      :checked-user-ids="checkedUserIds"
      @success="dataInfo.handleGrantSuccess"
    />
  </main>
</template>

<script setup lang="ts" name="Organization">
import { computed, reactive, toRefs } from 'vue';
import $utils from '@vue-scaffold/utils';
import OrganizationFormDialog from '@/views/organization/components/OrganizationFormDialog.vue';
import GrantOrgUsersDialog, { type GrantUserOption } from '@/views/organization/components/GrantOrgUsersDialog.vue';
import tableInfo from '@/views/organization/tables/Organization';
import { $apis } from '@/api/requests';
import type { OrganizationRecord } from '@/types/domain';


type OrgTreeItem = {
  id: number | string;
  parentId?: number | string | null;
  orgCode?: string;
  name?: string;
  leaderName?: string;
  sortOrder?: number;
  children?: OrgTreeItem[];
};

type UserPageItem = {
  id: number | string;
  username?: string;
  fullName?: string;
};

type OrgUserInfo = {
  userId?: number;
};

const dataInfo = reactive({
  organizations: [] as OrganizationRecord[],
  searchParams: {
    orgName: '',
    shortName: '',
  },
  dialogVisible: false,
  grantDialogVisible: false,
  selectedRecord: null as OrganizationRecord | null,
  selectedGrantOrg: null as OrganizationRecord | null,
  userOptions: [] as GrantUserOption[],
  checkedUserIds: [] as number[],
  loading: false,
  actionLoading: false,
  async getList() {
    this.loading = true;
    try {
      const result = await $apis.organizations.tree({});
      this.organizations = Array.isArray(result)
        ? result.map((item: OrgTreeItem) => mapOrganization(item))
        : [];
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
    return;
  },
  debounceSearch: $utils.Tool.debounce(function (this: any) {
    this.search();
  }, 300),
  openCreate() {
    this.selectedRecord = null;
    this.dialogVisible = true;
  },
  openEdit(row: OrganizationRecord) {
    this.selectedRecord = row;
    this.dialogVisible = true;
  },
  async openGrantUsers(row: OrganizationRecord) {
    this.actionLoading = true;
    try {
      const [userResult, orgUserResult] = await Promise.all([
        $apis.users.list({
          pageNum: 1,
          pageSize: 100,
          username: '',
          fullName: '',
          phone: '',
          email: '',
        }),
        $apis.orgUsers.list({
          orgId: Number(row.id),
        }),
      ]);
      this.userOptions = Array.isArray(userResult?.records)
        ? userResult.records.map((item: UserPageItem) => mapUserOption(item))
        : [];
      this.checkedUserIds = Array.isArray(orgUserResult)
        ? orgUserResult
            .map((item: OrgUserInfo) => Number(item.userId))
            .filter(item => !Number.isNaN(item))
        : [];
      this.selectedGrantOrg = row;
      this.grantDialogVisible = true;
    } finally {
      this.actionLoading = false;
    }
  },
  handleGrantSuccess() {
    this.grantDialogVisible = false;
  },
  async deleteOrganization(row: OrganizationRecord) {
    if (this.actionLoading) {
      return;
    }

    this.actionLoading = true;
    try {
      await $utils.Message.messageConfirm(`确认删除组织“${row.orgName}”吗？`);
      await $apis.organizations.delete({
        orgId: Number(row.id),
      });
      $utils.Message.messageAlert({ message: '组织删除成功' });
      await this.getList();
    } finally {
      this.actionLoading = false;
    }
  },
  async handleAction(row: OrganizationRecord, action: Record<string, any>) {
    if (dataInfo.actionLoading) {
      return;
    }

    const actionMap: Record<string, () => void | Promise<void>> = {
      grantUsers: () => dataInfo.openGrantUsers(row),
      edit: () => dataInfo.openEdit(row),
      delete: () => dataInfo.deleteOrganization(row),
    };

    await actionMap[action.key]?.();
  },
  async init() {
    await this.getList();
  },
});

const filteredOrganizations = computed(() => {
  const orgNameKeyword = dataInfo.searchParams.orgName.trim().toLowerCase();
  const shortNameKeyword = dataInfo.searchParams.shortName.trim().toLowerCase();

  if (!orgNameKeyword && !shortNameKeyword) {
    return dataInfo.organizations;
  }

  return filterOrganizationTree(dataInfo.organizations, item => {
    const orgName = item.orgName.toLowerCase();
    const shortName = (item.shortName ?? '').toLowerCase();
    const matchOrgName = !orgNameKeyword || orgName.includes(orgNameKeyword);
    const matchShortName = !shortNameKeyword || shortName.includes(shortNameKeyword);
    return matchOrgName && matchShortName;
  });
});

function mapOrganization(item: OrgTreeItem): OrganizationRecord {
  return {
    id: String(item.id),
    parentId:
      item.parentId === null || item.parentId === undefined
        ? null
        : String(item.parentId),
    orgName: item.name ?? '',
    shortName: item.orgCode ?? '',
    orderNum: Number(item.sortOrder ?? 1),
    remark: item.leaderName ?? '',
    children: Array.isArray(item.children) ? item.children.map(mapOrganization) : [],
  };
}

function mapUserOption(item: UserPageItem): GrantUserOption {
  return {
    id: String(item.id),
    name: item.fullName || item.username || '',
  };
}

function filterOrganizationTree(
  items: OrganizationRecord[],
  matcher: (item: OrganizationRecord) => boolean,
): OrganizationRecord[] {
  return items.reduce<OrganizationRecord[]>((result, item) => {
    const nextChildren = filterOrganizationTree(item.children ?? [], matcher);
    if (matcher(item) || nextChildren.length) {
      result.push({
        ...item,
        children: nextChildren,
      });
    }
    return result;
  }, []);
}

const { organizations, searchParams, userOptions, checkedUserIds, loading } = toRefs(dataInfo);

dataInfo.init();
</script>

<style scoped lang="scss">
.Organization-root {
  height: 100%;
}

.header-handle {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}
</style>
