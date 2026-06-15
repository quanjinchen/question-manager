// 用户管理的表格
export default {
  columns: [
    {
      label: '#',
      genre: '$ordinal',
      width: 64
    },
    {
      prop: 'username',
      label: '用户名',
      minWidth: 160
    },
    {
      prop: 'fullName',
      label: '姓名',
      minWidth: 180
    },
    {
      prop: 'phone',
      label: '手机号',
      minWidth: 160
    },
    {
      prop: 'idCard',
      label: '身份证号',
      minWidth: 220
    },
    {
      prop: 'email',
      label: '邮箱',
      minWidth: 220
    },
    {
      genre: '$tag',
      prop: 'status',
      label: '状态',
      width: 120,
      dictKey: 'userStatusList'
    },
    {
      fixed: 'right',
      label: '操作',
      genre: '$action',
      width: 300,
      actions: [
        { key: 'edit', label: '编辑', permissions: 'system:user:update' },
        { key: 'resetPassword', label: '重置密码', permissions: 'system:user:resetPassword', type: 'warning' },
        { key: 'delete', label: '删除', permissions: 'system:user:delete', type: 'danger' }
      ]
    }
  ]
};
