import { STORAGE_KEYS } from '@vue-scaffold/constants';

const noopStorage: Storage = {
  get length() {
    return 0;
  },
  clear() {},
  getItem() {
    return null;
  },
  key() {
    return null;
  },
  removeItem() {},
  setItem() {}
};

export function getBrowserStorage(): Storage {
  if (typeof window === 'undefined') {
    return noopStorage;
  }
  return window.localStorage;
}

export function getBrowserSessionStorage(): Storage {
  if (typeof window === 'undefined') {
    return noopStorage;
  }
  return window.sessionStorage;
}

function readJsonStorage<T>(storage: Storage, key: string, fallback?: T): T | null {
  const raw = storage.getItem(key);
  if (!raw) {
    return fallback ?? null;
  }
  try {
    return JSON.parse(raw) as T;
  } catch {
    return fallback ?? null;
  }
}

/**
 * 从 localStorage 中读取 JSON 数据。
 *
 * @param key 存储键名。
 * @param fallback 读取失败、无数据或 JSON 解析失败时返回的默认值。
 * @returns 解析后的存储值，或 fallback。
 * @example
 * ```ts
 * const user = getStorage("user", { name: "" });
 *
 * // localStorage.user 为 {"name":"张三"} 时，user => { name: "张三" }
 * ```
 */
export function getStorage<T>(key: string): T | null;
export function getStorage<T>(key: string, fallback: T): T;
export function getStorage<T>(key: string, fallback?: T): T | null {
  return readJsonStorage(getBrowserStorage(), key, fallback);
}

/**
 * 将值以 JSON 字符串形式写入 localStorage。
 *
 * @param key 存储键名。
 * @param value 需要存储的值。
 * @example
 * ```ts
 * setStorage("user", { name: "张三" });
 * ```
 */
export function setStorage(key: string, value: unknown) {
  getBrowserStorage().setItem(key, JSON.stringify(value));
}

/**
 * 从 sessionStorage 中读取 JSON 数据。
 *
 * @param key 存储键名。
 * @param fallback 读取失败、无数据或 JSON 解析失败时返回的默认值。
 * @returns 解析后的存储值，或 fallback。
 */
export function getSessionStorage<T>(key: string): T | null;
export function getSessionStorage<T>(key: string, fallback: T): T;
export function getSessionStorage<T>(key: string, fallback?: T): T | null {
  return readJsonStorage(getBrowserSessionStorage(), key, fallback);
}

/**
 * 将值以 JSON 字符串形式写入 sessionStorage。
 *
 * @param key 存储键名。
 * @param value 需要存储的值。
 */
export function setSessionStorage(key: string, value: unknown) {
  getBrowserSessionStorage().setItem(key, JSON.stringify(value));
}

/**
 * 从 localStorage 中移除指定键。
 *
 * @param key 存储键名。
 * @example
 * ```ts
 * removeStorage("user");
 * ```
 */
export function removeStorage(key: string) {
  getBrowserStorage().removeItem(key);
}

/**
 * 从 sessionStorage 中移除指定键。
 *
 * @param key 存储键名。
 */
export function removeSessionStorage(key: string) {
  getBrowserSessionStorage().removeItem(key);
}

/**
 * 清空 localStorage。
 *
 * @example
 * ```ts
 * cleartStorage();
 * ```
 */
export function cleartStorage() {
  getBrowserStorage().clear();
}

/**
 * 判断当前用户是否拥有指定权限。
 *
 * @param permission 单个权限标识或权限标识数组；为空时默认返回 `true`。
 * @returns 拥有任一传入权限时返回 `true`，否则返回 `false`。
 * @example
 * ```ts
 * const visible = hasStoredPermission("user:create");
 *
 * // visible => true
 * ```
 */
export function hasStoredPermission(permission?: string | string[]) {
  if (!permission) {
    return true;
  }
  const storage = getBrowserStorage();
  const permissions = storage.getItem(STORAGE_KEYS.permissions) ?? '';
  
  if (Array.isArray(permission)) {
    return permission.some(item => permissions.includes(item));
  }
  return permissions.includes(permission);
}
