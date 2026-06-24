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

export type QuestionCategoryRecord = {
  id: string | number;
  categoryName: string;
  description?: string;
  sortOrder: number;
  status: number;
  questionCount?: number;
  createTime?: string;
  updateTime?: string;
};

export type QuestionRecord = {
  id: string | number;
  categoryId: string | number;
  categoryName?: string;
  questionType: 'SINGLE' | 'MULTIPLE' | 'JUDGE' | 'QA';
  title: string;
  optionsJson?: string;
  answer?: string;
  analysis?: string;
  score: number;
  sortOrder: number;
  status: number;
  createTime?: string;
};

export type QuestionAnswerRecord = {
  id: string | number;
  userId: string | number;
  username?: string;
  fullName?: string;
  categoryId: string | number;
  categoryName?: string;
  totalScore: number;
  userScore: number;
  questionCount: number;
  correctCount: number;
  createTime?: string;
  details?: Array<Record<string, any>>;
};
