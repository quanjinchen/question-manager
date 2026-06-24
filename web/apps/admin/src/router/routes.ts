import { h } from 'vue';
import type { RouteRecordRaw } from 'vue-router';
import AdminLayout from '@/layouts/AdminLayout.vue';

const Login = () => import('@/views/login/Login.vue');
const Dashboard = () => import('@/views/dashboard/Dashboard.vue');
const Menu = () => import('@/views/system/Menu.vue');
const OperationLog = () => import('@/views/system/OperationLog.vue');
const Organization = () => import('@/views/organization/Organization.vue');
const User = () => import('@/views/user/User.vue');
const Role = () => import('@/views/system/Role.vue');
const QuestionCategory = () => import('@/views/question/QuestionCategory.vue');
const QuestionBankCategory = QuestionCategory;
const QuestionItem = () => import('@/views/question/QuestionItem.vue');
const QuestionRecord = () => import('@/views/question/QuestionRecord.vue');

// 固定路由：无论是否登录、是否有权限都需要提前存在的页面。
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: {
      title: '登录',
      noAuth: true
    }
  },
  {
    path: '/',
    name: 'root',
    component: AdminLayout,
    children: []
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: {
      render() {
        return h('div', { style: 'padding:24px' }, '页面不存在');
      }
    },
    meta: {
      title: '页面不存在',
      hideInMenu: true
    }
  }
];

// 本地页面路由表。登录后会按照后端 menuList.path 精确匹配这些页面，再动态挂到 root 下。
export const localRoutes: RouteRecordRaw[] = [
  {
    path: '/system/operation-log',
    name: 'system-operation-log',
    component: OperationLog,
    meta: {
      title: '日志审计',
      icon: 'Document',
      permissions: 'system:operationLog:query'
    }
  },
  {
    path: '/user',
    name: 'user',
    component: User,
    meta: {
      title: '用户列表',
      icon: 'User',
      permissions: 'system:user:query'
    }
  },
  {
    path: '/organization',
    name: 'organization',
    component: Organization,
    meta: {
      title: '组织管理',
      icon: 'Document',
      permissions: 'system:org:query'
    }
  },
  {
    path: '/system/menu',
    name: 'system-menu',
    component: Menu,
    meta: {
      title: '平台菜单',
      icon: 'Setting',
      permissions: 'system:menu:query'
    }
  },
  {
    path: '/system/role',
    name: 'system-role',
    component: Role,
    meta: {
      title: '平台角色',
      icon: 'Setting',
      permissions: 'system:role:query'
    }
  },
  {
    path: '/question/bank-category',
    name: 'question-bank-category',
    component: QuestionBankCategory,
    meta: {
      title: '题库分类',
      icon: 'FolderOpened',
      permissions: 'question:bankCategory:query'
    }
  },
  {
    path: '/question/category',
    name: 'question-category',
    component: QuestionCategory,
    meta: {
      title: '题库',
      icon: 'Collection',
      permissions: 'question:category:query'
    }
  },
  {
    path: '/question/item',
    name: 'question-item',
    component: QuestionItem,
    meta: {
      title: '题目管理',
      icon: 'Tickets',
      permissions: 'question:item:query'
    }
  },
  {
    path: '/question/record',
    name: 'question-record',
    component: QuestionRecord,
    meta: {
      title: '答题记录',
      icon: 'DocumentChecked',
      permissions: 'question:record:query'
    }
  },
  {
    path: '/index/baseInfo',
    name: 'index-baseInfo',
    component: Dashboard,
    meta: {
      title: '基础信息',
      icon: 'House',
      permissions: [
        'index:baseInfo',
        'system:index:baseInfo',
        'system:index:userNum',
        'system:index:userActive',
        'system:index:appRank',
        'system:index:userDevice'
      ]
    }
  }
];
