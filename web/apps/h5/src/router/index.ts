import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'root',
      meta: {
        title: '题库登录'
      },
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/login',
      name: 'login',
      meta: {
        title: '题库登录'
      },
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/categories',
      name: 'categories',
      meta: {
        title: '我的题库'
      },
      component: () => import('@/views/CategoryList.vue')
    },
    {
      path: '/answer/:categoryId',
      name: 'answer',
      meta: {
        title: '在线答题'
      },
      component: () => import('@/views/Answer.vue')
    },
    {
      path: '/score/:recordId',
      name: 'score',
      meta: {
        title: '答题成绩'
      },
      component: () => import('@/views/Score.vue')
    },
  ]
});

router.beforeEach(to => {
  document.title = to.meta?.title ? String(to.meta.title) : 'H5 Application';
  return true;
});

export default router;
