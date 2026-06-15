import { computed, ref } from 'vue';
import { defineStore } from 'pinia';
import type { RouteRecordRaw } from 'vue-router';
import { STORAGE_KEYS } from '@vue-scaffold/constants';
import $utils from '@vue-scaffold/utils';
import { filterRoutesByMenuList, isDirectoryMenu, isVisibleNavigationMenu } from '@/router/menu';
import type { MenuItem } from '@/router/menu';
import { localRoutes } from '@/router/routes';

type UserProfile = Record<string, any> & {
  id?: string | number;
  name?: string;
  fullName?: string;
  email?: string;
};

type LoginUserInfo = UserProfile & {
  menus?: MenuItem[];
  permissionCodes?: string[];
};

type PermissionRouteRegister = (route: RouteRecordRaw) => () => void;

const storage = $utils.Storage.getBrowserStorage();

export const useAdminStore: any = defineStore(
  'admin-store',
  () => {
    const title = ref(import.meta.env.VITE_APP_TITLE || document.title);
    const sidebarCollapsed = ref(false);
    const token = ref('');
    const userInfo = ref<UserProfile>({});
    const menuList = ref<MenuItem[]>([]);
    const permissions = ref<string[]>([]);
    const permissionRoutes = ref<RouteRecordRaw[]>([]);
    const isAddRoutes = ref(false);
    const permissionRouteRemovers: Array<() => void> = [];

    const sidebarWidth = computed(() => (sidebarCollapsed.value ? '72px' : '240px'));

    function initMenuData(menus: MenuItem[] = [], permissionCodes: string[] = permissions.value) {
      removePermissionRoutes();
      menuList.value = menus;
      permissions.value = permissionCodes;
      permissionRoutes.value = filterRoutesByMenuList(localRoutes, menus);
      return permissionRoutes.value;
    }

    function hasPermission(permission?: string | string[]) {
      if (!permission) {
        return true;
      }
      if (Array.isArray(permission)) {
        return permission.some(item => permissions.value.includes(item));
      }
      return permissions.value.includes(permission);
    }

    function removePermissionRoutes() {
      while (permissionRouteRemovers.length) {
        permissionRouteRemovers.pop()?.();
      }
      isAddRoutes.value = false;
    }

    function clearPermissionData() {
      removePermissionRoutes();
      menuList.value = [];
      permissions.value = [];
      permissionRoutes.value = [];
    }

    function registerPermissionRoutes(registerRoute: PermissionRouteRegister) {
      if (isAddRoutes.value) {
        return menuTree.value;
      }

      permissionRoutes.value.forEach(route => {
        permissionRouteRemovers.push(registerRoute(route));
      });
      isAddRoutes.value = true;

      return menuTree.value;
    }

    function setToken(value: string) {
      token.value = value;
    }

    function initUserInfo(info: LoginUserInfo = {}) {
      userInfo.value = info;
      initMenuData(info.menus || [], info.permissionCodes || []);
    }

    function clearSession() {
      token.value = '';
      userInfo.value = {};
      clearPermissionData();
    }

    function generateMenuTree(treeList: MenuItem[]): MenuItem[] {
      return treeList
        .filter(menuItem => {
          if (!isVisibleNavigationMenu(menuItem.menuType)) {
            return false;
          }
          // 目录允许没有 path，只要下面还有可展示子节点；菜单/页面必须有 path。
          if (isDirectoryMenu(menuItem.menuType)) {
            return true;
          }
          return Boolean(menuItem.path);
        })
        .map(menuItem => ({
          ...menuItem,
          children: menuItem.children ? generateMenuTree(menuItem.children) : undefined
        }))
        .filter(menuItem => {
          if (isDirectoryMenu(menuItem.menuType)) {
            return Boolean(menuItem.children?.length);
          }
          return true;
        });
    }

    const menuTree = computed(() => generateMenuTree(menuList.value));

    return {
      title,
      sidebarCollapsed,
      sidebarWidth,
      token,
      userInfo,
      menuList,
      permissions,
      permissionRoutes,
      isAddRoutes,
      menuTree,
      hasPermission,
      initMenuData,
      registerPermissionRoutes,
      setToken,
      initUserInfo,
      clearSession
    };
  },
  {
    persist: [
      {
        storage,
        key: STORAGE_KEYS.settings,
        pick: ['sidebarCollapsed']
      },
      {
        storage,
        key: STORAGE_KEYS.token,
        pick: ['token']
      },
      {
        storage,
        key: STORAGE_KEYS.profile,
        pick: ['userInfo']
      },
      {
        storage,
        key: STORAGE_KEYS.menus,
        pick: ['menuList']
      },
      {
        storage,
        key: STORAGE_KEYS.permissions,
        pick: ['permissions']
      }
    ]
  }
);

export function hasStoredPermission(permission?: string | string[]) {
  if (!permission) {
    return true;
  }
  return useAdminStore().hasPermission(permission);
}
