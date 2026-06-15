import type { RouteRecordRaw } from 'vue-router';

type MenuType = 'DIR' | 'MENU' | 'PAGE' | 'BTN';

export interface AccessMenuItem {
  menuName: string;
  path: string;
  menuType?: MenuType;
  icon?: string;
  menuCode?: string | string[];
  children?: AccessMenuItem[];
}

export type MenuItem = AccessMenuItem;

const MENU_TYPE_DIRECTORY: MenuType = 'DIR';
const MENU_TYPE_MENU: MenuType = 'MENU';
const MENU_TYPE_PAGE: MenuType = 'PAGE';
const MENU_TYPE_BTN: MenuType = 'BTN';



export function isDirectoryMenu(menuType?: string | number) {
  return menuType == 'DIR'
}

export function normalizeMenuType(menuType?: string | number): MenuType {
  if (menuType === MENU_TYPE_DIRECTORY || menuType === 'DIRECTORY' || menuType === 0) {
    return MENU_TYPE_DIRECTORY;
  }
  if (menuType === MENU_TYPE_PAGE || menuType === 'PAGE' || menuType === 2) {
    return MENU_TYPE_PAGE;
  }
  if (menuType === MENU_TYPE_BTN || menuType === 'BUTTON' || menuType === 3) {
    return MENU_TYPE_BTN;
  }
  return MENU_TYPE_MENU;
}

export function isVisibleNavigationMenu(menuType?: string | number) {
  return menuType === MENU_TYPE_DIRECTORY || menuType === MENU_TYPE_MENU;
}

// 递归处理菜单树，生成 path => menu 的映射表。
export function generatePathMap(menuList: AccessMenuItem[] = []) {
  const pathMap: Record<string, AccessMenuItem> = {};

  const walk = (items: AccessMenuItem[]) => {
    items.forEach(item => {
      if (item.path && !isDirectoryMenu(item.menuType)) {
        pathMap[item.path] = item;
      }
      if (item.children?.length) {
        walk(item.children);
      }
    });
  };

  walk(menuList);
  return pathMap;
}

// 本地页面路由是否可访问，核心只看后端菜单里有没有同 path 的节点。
export function filterRoutesByMenuList(routes: RouteRecordRaw[], menuList: AccessMenuItem[]) {
  const pathMap = generatePathMap(menuList);
  return routes.reduce<RouteRecordRaw[]>((result, route) => {
    if (pathMap[route.path]) {
      result.push({
        ...route
      });
    }
    return result;
  }, []);
}

// 根据最终可访问的路由树生成菜单树，避免重复维护一份菜单配置。
export function routesToMenus(routes: RouteRecordRaw[], basePath = ''): AccessMenuItem[] {
  return routes
    .filter(route => route.meta?.hideInMenu !== true)
    .map(route => {
      const path = route.path.startsWith('/')
        ? route.path
        : `${basePath}/${route.path}`.replace(/\/+/g, '/');
      return {
        menuName: String(route.meta?.title ?? route.name ?? path),
        path,
        menuType: route.children?.length ? 'DIR' : 'MENU',
        icon: route.meta?.icon as string | undefined,
        menuCode: route.meta?.permissions as string | string[] | undefined,
        children: route.children ? routesToMenus(route.children, path) : []
      };
    });
}

// 获取菜单树中第一个实际可进入的叶子节点路径，用于首页重定向。
export function getFirstMenuPath(menuTree: AccessMenuItem[]): string {
  for (const item of menuTree) {
    if (item.children && item.children.length > 0) {
      const childPath: string = getFirstMenuPath(item.children);
      if (childPath) {
        return childPath;
      }
    } else if (item.path && !isDirectoryMenu(item.menuType)) {
      return item.path;
    }
  }
  return '/';
}
