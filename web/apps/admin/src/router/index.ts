import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';
import { STORAGE_KEYS } from '@vue-scaffold/constants';
import $utils from '@vue-scaffold/utils';
import { getFirstMenuPath } from '@/router/menu';
import type { MenuItem } from '@/router/menu';
import { startProgress, stopProgress } from '@/router/progress';
import { useAdminStore } from '@/stores';
import { constantRoutes } from '@/router/routes';

// 先只挂载固定路由，权限路由在登录后按权限动态注入。
const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes
});

function resolveFirstPermissionPath() {
  const adminStore = useAdminStore();
  const firstMenuPath = getFirstMenuPath(adminStore.menuTree);
  return firstMenuPath && firstMenuPath !== '/' ? firstMenuPath : '';
}

function resolveStoredMenus(currentMenus: MenuItem[]) {
  const storedMenus = $utils.Storage.getStorage<MenuItem[] | { menuList?: MenuItem[] }>(
    STORAGE_KEYS.menus,
    currentMenus
  );
  return Array.isArray(storedMenus) ? storedMenus : storedMenus.menuList ?? [];
}

// 显式执行一次权限路由注册，既可以在登录成功后主动调用，也可以在刷新后的守卫里兜底调用。
export function ensurePermissionRoutes() {
  const adminStore = useAdminStore();
  return adminStore.registerPermissionRoutes((route: RouteRecordRaw) => router.addRoute('root', route));
}

router.beforeEach(async to => {
  // 每次切换路由时都启动顶部进度条，给页面切换一个明确反馈。
  startProgress();

  const adminStore = useAdminStore();
  const token = adminStore.token;
  const storedMenus = resolveStoredMenus(adminStore.menuList);
  const hasStoredMenus = Array.isArray(storedMenus) && storedMenus.length > 0;

  // 路由标题优先使用页面自身标题，没有时回退到系统标题。
  document.title = to.meta?.title
    ? `${String(to.meta.title)} | ${adminStore.title}`
    : adminStore.title;

  // 标记为 noAuth 的页面不参与登录校验，例如登录页、404。
  if (to.meta?.noAuth) {
    // localStorage 中已有 token 时，说明当前浏览器会话仍然处于登录态。
    if (to.path === '/login' && token) {
      return '/';
    }
    return true;
  }

  // 未登录时统一打回登录页，并记录原目标地址，登录后再跳回去。
  if (!token) {
    return {
      path: '/login',
      query: {
        redirect: to.fullPath
      }
    };
  }

  // 只要浏览器里还有菜单缓存，且本次会话尚未完成动态路由注入，
  // 就优先补注册，再继续进入当前目标地址。这样刷新动态页面时不会先被固定路由表吞掉。
  if (!adminStore.isAddRoutes && hasStoredMenus) {
    adminStore.initMenuData(adminStore.menuList.length ? adminStore.menuList : storedMenus);
    ensurePermissionRoutes();
    if (to.path === '/') {
      const redirectPath = resolveFirstPermissionPath();
      return redirectPath || true;
    }
    return to.fullPath;
  }

  // 登录成功后的第一次跳转，动态过滤并挂载当前用户有权访问的路由。
  if (!adminStore.isAddRoutes) {
    adminStore.initMenuData(adminStore.menuList);
    ensurePermissionRoutes();

    // 进入根路径时，自动跳到用户的第一个可访问菜单。
    if (to.path === '/') {
      const redirectPath = resolveFirstPermissionPath();
      return redirectPath || true;
    }

    // 动态加完路由后重新进入当前目标地址，避免首次访问命中不到新路由。
    return to.fullPath;
  }

  // 后续再次访问根路径时，同样跳到第一个可访问菜单。
  if (to.path === '/') {
    const redirectPath = resolveFirstPermissionPath();
    return redirectPath || true;
  }

  return true;
});

router.afterEach(() => {
  // 路由切换完成后关闭顶部进度条。
  stopProgress();
});

export default router;
