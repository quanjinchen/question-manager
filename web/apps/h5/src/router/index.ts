import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'root',
      meta: {
        title: '身份认证'
      },
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/login',
      name: 'login',
      meta: {
        title: '身份认证'
      },
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/auth-confirm',
      name: 'authConfirm',
      meta: {
        title: '授权确认'
      },
      component: () => import('@/views/AuthConfirm.vue')
    },
    {
      path: '/face-auth',
      name: 'faceAuth',
      meta: {
        title: '人脸认证'
      },
      component: () => import('@/views/FaceAuth.vue')
    },
    {
      path: '/auth-result',
      name: 'authResult',
      meta: {
        title: '认证结果'
      },
      component: () => import('@/views/AuthResult.vue')
    }
  ]
});

router.beforeEach(to => {
  document.title = to.meta?.title ? String(to.meta.title) : 'H5 Application';
  return true;
});

export default router;
