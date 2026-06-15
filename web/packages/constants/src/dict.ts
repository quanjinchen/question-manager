

// 字典表
export const dictStore = {
  userStatusList: [
    { value: 0, label: '启用', type: 'success' },
    { value: 1, label: '禁用', type: 'danger' },
  ],
  menuTypeList: [
    { value: "DIR", label: '目录', type: 'success' },
    { value: "MENU", label: '菜单', type: 'primary' },
    { value: "PAGE", label: '页面', type: 'warning' },
    { value: "BTN", label: '按钮', type: 'info' },
  ],
  booleanSuccessList: [
    { value: true, label: '成功', type: 'success' },
    { value: false, label: '失败', type: 'danger' },
  ],
  faceSelfRegisterList: [
    { value: true, label: '允许', type: 'success' },
    { value: false, label: '不允许', type: 'danger' },
  ],
  faceAuthApiTypeList: [
    { value: 1, label: '1:1接口', type: 'primary' },
    { value: 2, label: '1:N接口', type: 'success' },
  ],
  faceStatusList: [
    { value: 0, label: '失败', type: 'danger' },
    { value: 1, label: '成功', type: 'success' },
  ],
}
