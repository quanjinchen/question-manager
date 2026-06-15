import type { RoleRecord } from '@/types/domain';

export default {
  columns: [
    { key: 'ordinal', label: '#', genre: '$ordinal', width: 64 },
    { prop: 'roleName', label: '角色名称', minWidth: 180 },
    { prop: 'roleCode', label: '角色编码', minWidth: 180 },
    { prop: 'userNum', label: '用户数', width: 100 },
    { prop: 'userGroupNum', label: '分组数', width: 100 },
    { prop: 'updatedAt', label: '更新时间', genre: '$date', minWidth: 180 },
    {
      key: 'actions',
      label: '操作',
      fixed: "right",
      genre: '$action',
      width: 280,
      actions: [
        { key: 'grantMenus', label: '分配菜单', permissions: 'system:role:update' },
        { key: 'grantUsers', label: '分配用户', permissions: 'system:role:update' },
        { key: 'edit', label: '编辑', permissions: 'system:role:update' },
        {
          key: 'delete',
          label: '删除',
          permissions: 'system:role:delete',
          type: 'danger',
          visible: (row: RoleRecord) => !row.systemDefault,
        },
      ],
    },
  ],
} as const satisfies {
  columns: Array<Record<string, any>>;
};
