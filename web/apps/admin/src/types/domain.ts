export type UserRecord = {
  id: string;
  username: string;
  fullName: string;
  phone: string;
  idCard?: string;
  email: string;
  status: number | string;
  remark?: string;
};

export type AppRecord = {
  id: string;
  appName: string;
  appCode: string;
  clientId: string;
  clientSecret: string;
  remark?: string;
  createTime?: string;
  updateTime?: string;
};

export type OrganizationRecord = {
  id: string;
  parentId: string | null;
  orgName: string;
  shortName?: string;
  orderNum: number;
  remark?: string;
  children?: OrganizationRecord[];
};

export type MenuRecord = {
  id: string;
  parentId: string | null;
  menuType: 'DIR' | 'MENU' | 'PAGE' | 'BTN';
  menuName: string;
  path: string;
  icon?: string;
  menuCode: string;
  orderNum: number;
  enabled: boolean;
  children?: MenuRecord[];
};

export type RoleRecord = {
  id: string;
  roleCode: string;
  roleName: string;
  userNum: number;
  userGroupNum: number;
  systemDefault: boolean;
  remark?: string;
  createdAt: string;
  updatedAt: string;
};
