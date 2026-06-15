
export default {
  columns: [
    {
      key: 'ordinal',
      label: '#',
      genre: '$ordinal',
      width: 64,
    },
    {
      prop: 'appName',
      label: '应用名称',
      minWidth: 180,
    },
    {
      prop: 'appCode',
      label: '应用编码',
      minWidth: 180,
    },
    {
      prop: 'clientId',
      label: '应用账号',
      minWidth: 260,
    },
    {
      prop: 'clientSecret',
      label: '应用密钥',
      minWidth: 320,
    },
    {
      prop: 'allowFaceSelfRegister',
      label: '允许人脸自注册',
      genre: '$tag',
      dictKey: 'faceSelfRegisterList',
      minWidth: 140,
    },
    {
      prop: 'remark',
      label: '备注',
      minWidth: 200,
    },
    {
      prop: 'updateTime',
      label: '更新时间',
      genre: '$date',
      minWidth: 180,
    },
    {
      key: 'actions',
      label: '操作',
      fixed: 'right',
      genre: '$action',
      width: 180,
      actions: [
        { key: 'edit', label: '编辑', permissions: 'system:app:update' },
        { key: 'delete', label: '删除', permissions: 'system:app:delete', type: 'danger' },
      ],
    },
  ],
} as const satisfies {
  columns: Array<Record<string, any>>;
};
