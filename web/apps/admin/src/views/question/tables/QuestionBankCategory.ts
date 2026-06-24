export default {
  columns: [
    { key: 'ordinal', label: '#', genre: '$ordinal', width: 64 },
    { prop: 'categoryName', label: '分类名称', minWidth: 180 },
    { prop: 'description', label: '描述', minWidth: 260 },
    { prop: 'bankCount', label: '题库数', width: 100 },
    { prop: 'sortOrder', label: '排序', width: 100 },
    { prop: 'status', label: '状态', genre: '$slot', width: 100 },
    { prop: 'updateTime', label: '更新时间', genre: '$date', minWidth: 180 },
    {
      key: 'actions',
      label: '操作',
      fixed: 'right',
      genre: '$action',
      width: 180,
      actions: [
        { key: 'edit', label: '编辑', permissions: 'question:bankCategory:update' },
        { key: 'delete', label: '删除', permissions: 'question:bankCategory:delete', type: 'danger' },
      ],
    },
  ],
} as const;
