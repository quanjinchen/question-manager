// copy to vben-admin

const toString = Object.prototype.toString;

/**
 * 判断值的内部类型是否匹配指定类型名。
 *
 * @param val 输入值。
 * @param type 类型名，例如 `String`、`Object`。
 * @returns 类型匹配时返回 `true`。
 * @example
 * ```ts
 * const matched = is("abc", "String");
 *
 * // matched => true
 * ```
 */
export function is(val: unknown, type: string) {
  return toString.call(val) === `[object ${type}]`;
}

/**
 * 判断值是否不是 `undefined`。
 *
 * @param val 输入值。
 * @returns 不是 `undefined` 时返回 `true`。
 * @example
 * ```ts
 * isDef(null);
 *
 * // => true
 * ```
 */
export function isDef<T = unknown>(val?: T): val is T {
  return typeof val !== "undefined";
}

/**
 * 判断值是否是 `undefined`。
 *
 * @param val 输入值。
 * @returns 是 `undefined` 时返回 `true`。
 * @example
 * ```ts
 * isUnDef(undefined);
 *
 * // => true
 * ```
 */
export function isUnDef<T = unknown>(val?: T): val is T {
  return !isDef(val);
}

/**
 * 判断值是否是普通对象。
 *
 * @param val 输入值。
 * @returns 是普通对象时返回 `true`。
 * @example
 * ```ts
 * isObject({ id: 1 });
 *
 * // => true
 * ```
 */
export function isObject(val: any): val is Record<any, any> {
  return val !== null && is(val, "Object");
}

/**
 * 是否为空
 * @param val
 * @returns 空字符串、空数组、空对象、空 Map、空 Set、null 或 undefined 返回 `true`。
 * @example
 * ```ts
 * isEmpty([]);
 *
 * // => true
 * ```
 */
export function isEmpty<T = unknown>(val: T): val is T {
  if (val === null) {
    return true;
  }
  if (val === undefined) {
    return true;
  }
  if (isArray(val) || isString(val)) {
    return val.length === 0;
  }

  if (val instanceof Map || val instanceof Set) {
    return val.size === 0;
  }

  if (isObject(val)) {
    return Object.keys(val).length === 0;
  }

  return false;
}

/**
 * 是否不为空
 * @param val
 * @returns 非空时返回 `true`。
 * @example
 * ```ts
 * isNotEmpty([1]);
 *
 * // => true
 * ```
 */
export function isNotEmpty<T = unknown>(val: T): val is T {
  return !isEmpty(val);
}

/**
 * 是否日期类型
 * @param val
 * @returns 是 Date 对象时返回 `true`。
 * @example
 * ```ts
 * isDate(new Date());
 *
 * // => true
 * ```
 */
export function isDate(val: unknown): val is Date {
  return is(val, "Date");
}

/**
 * 是否为null
 * @param val
 * @returns 是 `null` 时返回 `true`。
 * @example
 * ```ts
 * isNull(null);
 *
 * // => true
 * ```
 */
export function isNull(val: unknown): val is null {
  return val === null;
}

/**
 * 判断值是否同时满足 `null` 和 `undefined`。
 *
 * @param val 输入值。
 * @returns 由于值不可能同时为 null 和 undefined，通常返回 `false`。
 * @example
 * ```ts
 * isNullAndUnDef(null);
 *
 * // => false
 * ```
 */
export function isNullAndUnDef(val: unknown): val is null | undefined {
  return isUnDef(val) && isNull(val);
}

/**
 * 判断值是否是 `null` 或 `undefined`。
 *
 * @param val 输入值。
 * @returns 是 `null` 或 `undefined` 时返回 `true`。
 * @example
 * ```ts
 * isNullOrUnDef(undefined);
 *
 * // => true
 * ```
 */
export function isNullOrUnDef(val: unknown): val is null | undefined {
  return isUnDef(val) || isNull(val);
}

/**
 * 是否为数字
 * @param val
 * @returns 是 Number 类型时返回 `true`。
 * @example
 * ```ts
 * isNumber(1);
 *
 * // => true
 * ```
 */
export function isNumber(val: unknown): val is number {
  return is(val, "Number");
}

/**
 * 判断值是否是 Promise 对象。
 *
 * @param val 输入值。
 * @returns 是 Promise 时返回 `true`。
 * @example
 * ```ts
 * isPromise(Promise.resolve());
 *
 * // => true
 * ```
 */
export function isPromise<T = any>(val: unknown): val is Promise<T> {
  return is(val, "Promise") && isObject(val) && isFunction(val.then) && isFunction(val.catch);
}

/**
 * 判断值是否是字符串。
 *
 * @param val 输入值。
 * @returns 是 String 类型时返回 `true`。
 * @example
 * ```ts
 * isString("abc");
 *
 * // => true
 * ```
 */
export function isString(val: unknown): val is string {
  return is(val, "String");
}

/**
 * 判断值是否是函数。
 *
 * @param val 输入值。
 * @returns 是函数时返回 `true`。
 * @example
 * ```ts
 * isFunction(() => {});
 *
 * // => true
 * ```
 */
export function isFunction(val: unknown): val is Function {
  return typeof val === "function";
}

/**
 * 判断值是否是布尔值。
 *
 * @param val 输入值。
 * @returns 是 Boolean 类型时返回 `true`。
 * @example
 * ```ts
 * isBoolean(false);
 *
 * // => true
 * ```
 */
export function isBoolean(val: unknown): val is boolean {
  return is(val, "Boolean");
}

/**
 * 判断值是否是正则表达式。
 *
 * @param val 输入值。
 * @returns 是 RegExp 类型时返回 `true`。
 * @example
 * ```ts
 * isRegExp(/abc/);
 *
 * // => true
 * ```
 */
export function isRegExp(val: unknown): val is RegExp {
  return is(val, "RegExp");
}

/**
 * 判断值是否是数组。
 *
 * @param val 输入值。
 * @returns 是数组时返回 `true`。
 * @example
 * ```ts
 * isArray([]);
 *
 * // => true
 * ```
 */
export function isArray(val: any): val is Array<any> {
  return val && Array.isArray(val);
}

/**
 * 判断值是否是浏览器 window 对象。
 *
 * @param val 输入值。
 * @returns 在浏览器环境中传入 window 时返回 `true`。
 * @example
 * ```ts
 * isWindow(window);
 *
 * // => true
 * ```
 */
export function isWindow(val: any): val is Window {
  return typeof window !== "undefined" && is(val, "Window");
}

/**
 * 判断值是否是 DOM Element。
 *
 * @param val 输入值。
 * @returns 是 Element 时返回 `true`。
 * @example
 * ```ts
 * isElement(document.body);
 *
 * // => true
 * ```
 */
export function isElement(val: unknown): val is Element {
  return isObject(val) && !!val.tagName;
}

/**
 * 判断值是否是 Map。
 *
 * @param val 输入值。
 * @returns 是 Map 时返回 `true`。
 * @example
 * ```ts
 * isMap(new Map());
 *
 * // => true
 * ```
 */
export function isMap(val: unknown): val is Map<any, any> {
  return is(val, "Map");
}

/**
 * 当前是否是服务端环境。
 *
 * @example
 * ```ts
 * if (isServer) {
 *   // 当前没有 window
 * }
 * ```
 */
export const isServer = typeof window === "undefined";

/**
 * 当前是否是客户端环境。
 *
 * @example
 * ```ts
 * if (isClient) {
 *   // 可以访问 window
 * }
 * ```
 */
export const isClient = typeof window !== "undefined";

/**
 * 判断字符串是否是 URL。
 *
 * @param path 待判断字符串。
 * @returns 符合 URL 格式时返回 `true`。
 * @example
 * ```ts
 * isUrl("https://example.com");
 *
 * // => true
 * ```
 */
export function isUrl(path: string): boolean {
  const reg =
    /(((^https?:(?:\/\/)?)(?:[-:&=+$,\w]+@)?[A-Za-z0-9.-]+(?::\d+)?|(?:www.|[-:&=+$,\w]+@)[A-Za-z0-9.-]+)((?:\/[+~%\/.\w-_]*)?\??(?:[-+=&%@.\w_]*)#?(?:[\w]*))?)$/;
  return reg.test(path);
}

/**
 * 判断系统是否偏好深色模式。
 *
 * @returns 浏览器深色模式媒体查询匹配时返回 `true`。
 * @example
 * ```ts
 * const dark = isDark();
 *
 * // dark => true 或 false
 * ```
 */
export function isDark(): boolean {
  return window.matchMedia("(prefers-color-scheme: dark)").matches;
}

// 是否是图片链接
/**
 * 判断路径是否是图片地址。
 *
 * @param path 待判断路径。
 * @returns 是图片地址时返回 `true`。
 * @example
 * ```ts
 * isImgPath("https://example.com/a.png");
 *
 * // => true
 * ```
 */
export function isImgPath(path: string): boolean {
  return /(https?:\/\/|data:image\/).*?\.(png|jpg|jpeg|gif|svg|webp|ico)/gi.test(path);
}

/**
 * 判断值是否是空表单值。
 *
 * @param val 输入值。
 * @returns 值为 `""`、`null` 或 `undefined` 时返回 `true`。
 * @example
 * ```ts
 * isEmptyVal("");
 *
 * // => true
 * ```
 */
export function isEmptyVal(val: any): boolean {
  return val === "" || val === null || val === undefined;
}
