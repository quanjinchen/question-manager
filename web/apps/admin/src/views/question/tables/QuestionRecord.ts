export default {
  columns: [
    { key: 'ordinal', label: '#', genre: '$ordinal', width: 64 },
    { prop: 'fullName', label: '用户姓名', minWidth: 140 },
    { prop: 'username', label: '用户名', minWidth: 140 },
    { prop: 'categoryName', label: '题目分类', minWidth: 180 },
    { prop: 'userScore', label: '得分', width: 100 },
    { prop: 'totalScore', label: '满分', width: 100 },
    { prop: 'questionCount', label: '题目数', width: 100 },
    { prop: 'correctCount', label: '正确数', width: 100 },
    { prop: 'createTime', label: '提交时间', genre: '$date', minWidth: 180 },
    {
      key: 'actions',
      label: '操作',
      fixed: 'right',
      genre: '$action',
      width: 120,
      actions: [
        { key: 'detail', label: '详情', permissions: 'question:record:query' },
      ],
    },
  ],
} as const;

