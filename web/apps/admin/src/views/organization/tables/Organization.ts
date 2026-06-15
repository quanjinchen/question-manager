import type { OrganizationRecord } from '@/types/domain';

export default {
  columns: [
    { prop: 'orgName', label: '组织名称', minWidth: 180 },
    { prop: 'shortName', label: '组织简称', minWidth: 120 },
    { prop: 'orderNum', label: '排序', minWidth: 80 },
    { prop: 'remark', label: '备注', minWidth: 180 },
    {
      key: 'actions',
      label: '操作',
      genre: '$action',
      width: 280,
      actions: [
        { key: 'grantUsers', label: '分配用户', permissions: 'system:org:update' },
        { key: 'edit', label: '编辑', permissions: 'system:org:update' },
        { key: 'delete', label: '删除', permissions: 'system:org:delete', type: 'danger' },
      ],
    },
  ],
} as const satisfies {
  columns: Array<Record<string, any>>;
};
