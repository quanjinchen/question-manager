export default {
  columns: [
    { key: 'ordinal', label: '#', genre: '$ordinal', width: 64 },
    { prop: 'title', label: '题干', minWidth: 280 },
    { prop: 'categoryName', label: '所属分类', minWidth: 160 },
    { prop: 'questionType', label: '题型', genre: '$slot', width: 110 },
    { prop: 'score', label: '分值', width: 100 },
    { prop: 'sortOrder', label: '排序', width: 100 },
    { prop: 'status', label: '状态', genre: '$slot', width: 100 },
    { prop: 'createTime', label: '创建时间', genre: '$date', minWidth: 180 },
    {
      key: 'actions',
      label: '操作',
      fixed: 'right',
      genre: '$action',
      width: 160,
      actions: [
        { key: 'edit', label: '编辑', permissions: 'question:item:update' },
        { key: 'delete', label: '删除', permissions: 'question:item:delete', type: 'danger' },
      ],
    },
  ],
} as const;

