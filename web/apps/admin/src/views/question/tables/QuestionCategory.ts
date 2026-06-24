export default {
  columns: [
    { key: 'ordinal', label: '#', genre: '$ordinal', width: 64 },
    { prop: 'categoryName', label: '题库名称', minWidth: 180 },
    { prop: 'bankCategoryName', label: '题库分类', minWidth: 160 },
    { prop: 'description', label: '描述', minWidth: 240 },
    { prop: 'questionCount', label: '题目数', width: 100 },
    { prop: 'sortOrder', label: '排序', width: 100 },
    { prop: 'status', label: '状态', genre: '$slot', width: 100 },
    { prop: 'updateTime', label: '更新时间', genre: '$date', minWidth: 180 },
    {
      key: 'actions',
      label: '操作',
      fixed: 'right',
      genre: '$action',
      width: 260,
      actions: [
        { key: 'grant', label: '授权', permissions: 'question:category:grant' },
        { key: 'edit', label: '编辑', permissions: 'question:category:update' },
        { key: 'delete', label: '删除', permissions: 'question:category:delete', type: 'danger' },
      ],
    },
  ],
} as const;
