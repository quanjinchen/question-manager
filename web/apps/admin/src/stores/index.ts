import { createPinia } from 'pinia';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';

// 统一创建 admin 应用使用的 pinia 实例，并在这里集中挂载通用插件。
export function createWorkspacePinia() {
  const pinia = createPinia();

  // 为需要持久化的 store 提供 localStorage 持久化能力。
  pinia.use(piniaPluginPersistedstate);

  // 给每个 store 注入一个通用的 $reset，方便退出登录或页面重置时恢复初始状态。
  pinia.use(({ store }) => {
    const initialState = JSON.parse(JSON.stringify(store.$state));
    store.$reset = () => {
      store.$state = JSON.parse(JSON.stringify(initialState));
    };
  });
  return pinia;
}

export * from './admin';
