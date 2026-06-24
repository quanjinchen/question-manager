<template>
  <AppDialog
    v-model="visible"
    :modal-props="modalProps"
    :footer-props="footerProps"
  >
    <div class="GrantRoleUsersDialog-root">
      <section class="candidate-panel">
        <div class="panel-head">
          <strong>候选用户</strong>
          <span>已关联用户不可勾选</span>
        </div>

        <div class="toolbar">
          <AppInput
            v-model="searchParams.username"
            placeholder="用户名"
            :icon-props="{ place: 'suffix', name: 'Search' }"
            :input-props="{ clearable: true }"
            @input="dataInfo.debounceSearch()"
          />
          <AppInput
            v-model="searchParams.fullName"
            placeholder="姓名"
            :icon-props="{ place: 'suffix', name: 'Search' }"
            :input-props="{ clearable: true }"
            @input="dataInfo.debounceSearch()"
          />
        </div>

        <el-table
          ref="tableRef"
          v-loading="loading"
          :data="list"
          row-key="id"
          height="420"
          @selection-change="dataInfo.handleSelectionChange"
        >
          <el-table-column
            type="selection"
            width="52"
            reserve-selection
            :selectable="dataInfo.isSelectable"
          />
          <el-table-column prop="username" label="用户名" min-width="140" />
          <el-table-column prop="fullName" label="姓名" min-width="140" />
          <el-table-column label="状态" width="120">
            <template #default="{ row }">
              <el-tag v-if="dataInfo.isAssigned(row)" type="info">已关联</el-tag>
              <el-tag v-else type="success">可新增</el-tag>
            </template>
          </el-table-column>
        </el-table>

        <div class="pager-wrap">
          <AppPager
            v-model:page-index="pageInfo.pageNum"
            v-model:page-size="pageInfo.pageSize"
            :total="total"
            @change="dataInfo.getList()"
          />
        </div>
      </section>

      <section class="selected-panel">
        <div class="panel-head">
          <strong>关联结果预览</strong>
          <span>{{ currentAssignedUsers.length + selectedUsers.length }} 人</span>
        </div>

        <div class="selected-group">
          <div class="group-title">当前保留绑定</div>
          <div v-if="currentAssignedUsers.length" class="selected-list">
            <div
              v-for="item in currentAssignedUsers"
              :key="`bound-${item.id}`"
              class="selected-item"
            >
              <div>
                <div class="selected-name">{{ item.fullName || item.username }}</div>
                <div class="selected-meta">{{ item.username }}</div>
              </div>
              <AppButton
                :button-props="{ text: true, type: 'danger' }"
                @click="dataInfo.removeAssigned(item.id)"
              >
                解绑
              </AppButton>
            </div>
          </div>
          <div v-else class="empty-inline">暂无保留绑定用户</div>
        </div>

        <div class="selected-group">
          <div class="group-title">待新增绑定</div>
          <div v-if="selectedUsers.length" class="selected-list">
            <div
              v-for="item in selectedUsers"
              :key="`selected-${item.id}`"
              class="selected-item"
            >
              <div>
                <div class="selected-name">{{ item.fullName || item.username }}</div>
                <div class="selected-meta">{{ item.username }}</div>
              </div>
              <AppButton
                :button-props="{ text: true, type: 'danger' }"
                @click="dataInfo.removeSelected(item.id)"
              >
                移除
              </AppButton>
            </div>
          </div>
          <div v-else class="empty-inline">暂无待新增用户</div>
        </div>

        <div v-if="!currentAssignedUsers.length && !selectedUsers.length" class="empty-state">
          请选择左侧用户或在右侧解绑已有用户
        </div>
      </section>
    </div>
  </AppDialog>
</template>

<script setup lang="ts" name="GrantRoleUsersDialog">
import { computed, nextTick, reactive, ref, toRefs, watch } from 'vue';
import $utils from '@vue-scaffold/utils';
import { useVModel } from '@vue-scaffold/hooks';
import type { TableInstance } from 'element-plus';
import { $apis } from '@/api/requests';
import type { RoleRecord } from '@/types/domain';

type CandidateUser = {
  id: string;
  username: string;
  fullName: string;
};

type RoleUserInfo = {
  userId?: number | string;
  username?: string;
  fullName?: string;
};

const props = defineProps<{
  modelValue: boolean;
  selectItem?: RoleRecord | null;
}>();

const emit = defineEmits<{
  'update:modelValue': [boolean];
  success: [];
}>();

const visible = useVModel(props, emit as any);
const tableRef = ref<TableInstance>();

const modalProps = computed(() => ({
  title: `分配关联用户${props.selectItem ? ` - ${props.selectItem.roleName}` : ''}`,
  width: 900,
}));

const footerProps = computed(() => ({
  buttons: [
    { text: '取消', close: true, buttonProps: {} },
    {
      text: submitLoading.value ? '保存中...' : '确认分配',
      close: false,
      buttonProps: { type: 'primary', loading: submitLoading.value },
      click: () => dataInfo.handleSubmit(),
    },
  ],
}));

const dataInfo = reactive({
  searchParams: {
    username: '',
    fullName: '',
  },
  pageInfo: {
    pageNum: 1,
    pageSize: 10,
  },
  total: 0,
  list: [] as CandidateUser[],
  loading: false,
  submitLoading: false,
  assignedMap: {} as Record<string, CandidateUser>,
  selectedMap: {} as Record<string, CandidateUser>,
  get params() {
    return {
      pageNum: this.pageInfo.pageNum,
      pageSize: this.pageInfo.pageSize,
      username: this.searchParams.username,
      fullName: this.searchParams.fullName,
    };
  },
  isAssigned(row: CandidateUser) {
    return Boolean(this.assignedMap[String(row.id)]);
  },
  isSelectable(row: CandidateUser) {
    return !dataInfo.isAssigned(row);
  },
  handleSelectionChange(rows: CandidateUser[]) {
    const currentPageIds = dataInfo.list.map(item => String(item.id));
    for (const id of currentPageIds) {
      delete dataInfo.selectedMap[id];
    }
    for (const row of rows) {
      if (!dataInfo.isAssigned(row)) {
        dataInfo.selectedMap[String(row.id)] = row;
      }
    }
  },
  removeSelected(userId: string) {
    delete this.selectedMap[String(userId)];
    const row = this.list.find(item => String(item.id) === String(userId));
    if (row) {
      tableRef.value?.toggleRowSelection(row, false);
    }
  },
  search() {
    this.pageInfo.pageNum = 1;
    this.getList();
  },
  debounceSearch: $utils.Tool.debounce(function (this: any) {
    this.search();
  }, 300),
  async loadAssignedUsers() {
    if (!props.selectItem?.id) {
      this.assignedMap = {};
      return;
    }
    const result = await $apis.roles.roleUsers({
      roleId: Number(props.selectItem.id),
    });
    const assignedUsers = Array.isArray(result)
      ? result.map((item: RoleUserInfo) => ({
          id: String(item.userId ?? ''),
          username: String(item.username ?? ''),
          fullName: String(item.fullName ?? ''),
        }))
      : [];
    this.assignedMap = assignedUsers.reduce<Record<string, CandidateUser>>((accumulator, item) => {
      accumulator[item.id] = item;
      return accumulator;
    }, {});
  },
  removeAssigned(userId: string) {
    delete this.assignedMap[String(userId)];
  },
  async getList() {
    if (!props.selectItem?.id) {
      return;
    }
    this.loading = true;
    try {
      const result = await $apis.users.list(this.params);
      this.list = Array.isArray(result?.records)
        ? result.records.map((item: Record<string, any>) => ({
            id: String(item.id ?? ''),
            username: String(item.username ?? ''),
            fullName: String(item.fullName ?? ''),
          }))
        : [];
      this.total = Number(result?.total ?? 0);
      await nextTick();
      tableRef.value?.clearSelection();
      for (const row of this.list) {
        if (this.selectedMap[String(row.id)]) {
          tableRef.value?.toggleRowSelection(row, true);
        }
      }
    } finally {
      this.loading = false;
    }
  },
  resetState() {
    tableRef.value?.clearSelection();
    this.searchParams.username = '';
    this.searchParams.fullName = '';
    this.pageInfo.pageNum = 1;
    this.pageInfo.pageSize = 10;
    this.total = 0;
    this.list = [];
    this.loading = false;
    this.submitLoading = false;
    this.assignedMap = {};
    this.selectedMap = {};
  },
  async init() {
    this.resetState();
    if (!props.selectItem?.id) {
      return;
    }
    await this.loadAssignedUsers();
    await this.getList();
  },
  async handleSubmit() {
    if (!props.selectItem?.id || this.submitLoading) {
      return;
    }
    this.submitLoading = true;
    try {
      const selectedUserIds = Object.keys(this.selectedMap)
        .map(item => Number(item))
        .filter(item => !Number.isNaN(item));
      const assignedUserIds = Object.keys(this.assignedMap)
        .map(item => Number(item))
        .filter(item => !Number.isNaN(item));
      await $apis.roles.grantUsers({
        roleId: Number(props.selectItem.id),
        userIds: [...assignedUserIds, ...selectedUserIds],
      });
      $utils.Message.messageAlert({ message: '角色关联用户分配成功' });
      visible.value = false;
      emit('success');
    } finally {
      this.submitLoading = false;
    }
  },
});

const selectedUsers = computed(() => Object.values(dataInfo.selectedMap));
const currentAssignedUsers = computed(() => Object.values(dataInfo.assignedMap));
const { searchParams, pageInfo, total, list, loading, submitLoading } = toRefs(dataInfo);

watch(
  () => visible.value,
  async value => {
    if (value) {
      return;
    }
    dataInfo.resetState();
  },
);

watch(
  () => [visible.value, props.selectItem?.id] as const,
  async ([isVisible, roleId], previousValue) => {
    const [prevVisible, prevRoleId] = previousValue ?? [false, undefined];
    if (!isVisible || !roleId) {
      return;
    }
    if (isVisible !== prevVisible || roleId !== prevRoleId) {
      await dataInfo.init();
    }
  },
  { immediate: true },
);
</script>

<style scoped lang="scss">
.GrantRoleUsersDialog-root {
  display: grid;
  grid-template-columns: minmax(0, 1.8fr) minmax(280px, 1fr);
  gap: 20px;
}

.candidate-panel,
.selected-panel {
  display: flex;
  flex-direction: column;
  min-height: 520px;
  max-height: 520px;
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #fff;
  overflow: hidden;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.panel-head strong {
  font-size: 16px;
  color: #111827;
}

.panel-head span {
  font-size: 12px;
  color: #6b7280;
}

.toolbar {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 12px;
}

.pager-wrap {
  margin-top: 12px;
}

.selected-panel {
  overflow-y: auto;
}

.selected-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.selected-group + .selected-group {
  margin-top: 16px;
}

.selected-group {
  flex-shrink: 0;
}

.group-title {
  margin-bottom: 10px;
  color: #374151;
  font-size: 13px;
  font-weight: 600;
}

.selected-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  background: #f9fafb;
}

.selected-name {
  color: #111827;
  font-weight: 600;
}

.selected-meta {
  margin-top: 4px;
  color: #6b7280;
  font-size: 12px;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 120px;
  color: #9ca3af;
  text-align: center;
}

.empty-inline {
  padding: 12px 0;
  color: #9ca3af;
  font-size: 13px;
}

@media (max-width: 960px) {
  .GrantRoleUsersDialog-root {
    grid-template-columns: 1fr;
  }

  .toolbar {
    grid-template-columns: 1fr;
  }
}
</style>
