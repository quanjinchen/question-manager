export default {
  columns: [
    { key: 'ordinal', label: '#', genre: '$ordinal', width: 64 },
    {
      prop: 'authApiType',
      label: '认证接口',
      genre: '$tag',
      width: 120,
      dictKey: 'faceAuthApiTypeList',
    },
    { prop: 'ip', label: '请求 IP', minWidth: 150 },
    { prop: 'createTime', label: '认证时间', genre: '$date', minWidth: 180 },
    { prop: 'appName', label: '应用名称', minWidth: 180 },
    { prop: 'authFullName', label: '认证人姓名', minWidth: 160 },
    { prop: 'status', label: '状态', genre: '$tag', width: 120, dictKey: 'booleanSuccessList' },
    { prop: 'errmsg', label: '失败原因', minWidth: 220 },
  ],
} as const satisfies {
  columns: Array<Record<string, any>>;
};
