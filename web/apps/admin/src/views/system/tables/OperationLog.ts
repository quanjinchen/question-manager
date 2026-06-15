export default {
  columns: [
    { key: 'ordinal', label: '#', genre: '$ordinal', width: 64 },
    { prop: 'moduleName', label: '模块', minWidth: 140 },
    { prop: 'actionName', label: '动作', minWidth: 140 },
    { prop: 'operatorName', label: '操作人', minWidth: 140 },
    { prop: 'requestPath', label: '请求路径', minWidth: 260 },
    {
      prop: 'successFlag',
      label: '结果',
      genre: '$tag',
      width: 120,
      dictKey: "booleanSuccessList"
    },
    { prop: 'requestTime', label: '操作时间', genre: '$date', minWidth: 180 },
  ],
} as const satisfies {
  columns: Array<Record<string, any>>;
};
