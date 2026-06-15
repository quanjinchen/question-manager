import type { Router } from 'vue-router';

// 提供一个可复用的 router 单例引用，方便应用其他模块按需获取当前路由实例。
let routerInstance: Router | null = null;

export function setRouterInstance(router: Router) {
  routerInstance = router;
}

// 读取已注入的 router 实例。
export function getRouterInstance() {
  return routerInstance;
}
