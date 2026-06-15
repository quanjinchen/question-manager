import type { MenuRecord } from '@/types/domain';

export default {
  columns: [
    { prop: 'menuName', label: '菜单名称', minWidth: 180 },
    { prop: 'path', label: '路由路径', minWidth: 220 },
    { prop: 'menuCode', label: '权限编码', minWidth: 220 },
    { prop: 'orderNum', label: '排序', width: 100 },
    {
      prop: 'menuType',
      label: '类型',
      genre: "$tag",
      width: 100,
      dictKey: "menuTypeList",
    },
    {
      key: 'actions',
      label: '操作',
      genre: '$action',
      fixed: "right",
      width: 200,
      actions: [
        { key: 'create', label: '新增下级', permissions: 'system:menu:update' },
        { key: 'edit', label: '编辑', permissions: 'system:menu:update' },
        { key: 'delete', label: '删除', permissions: 'system:menu:delete', type: 'danger' },
      ],
    },
  ],
} as const satisfies {
  columns: Array<Record<string, any>>;
};
