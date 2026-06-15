/**
 * 防抖函数，在连续触发时只执行最后一次。
 *
 * @param fn 需要防抖的函数。
 * @param wait 防抖等待时间，单位毫秒。
 * @returns 包装后的防抖函数。
 * @example
 * ```ts
 * const search = debounce((keyword: string) => query(keyword), 300);
 * search("admin");
 * ```
 */
export function debounce<T extends (...args: any[]) => void>(fn: T, wait = 300) {
  let timer: number | undefined;
  return (...args: Parameters<T>) => {
    window.clearTimeout(timer);
    timer = window.setTimeout(() => fn(...args), wait);
  };
}

/**
 * 节流函数，在指定时间窗口内限制执行频率。
 *
 * @param fn 需要节流的函数。
 * @param wait 节流间隔，单位毫秒。
 * @returns 包装后的节流函数。
 * @example
 * ```ts
 * const resize = throttle(() => refreshLayout(), 200);
 * window.addEventListener("resize", resize);
 * ```
 */
export function throttle<T extends (...args: any[]) => void>(fn: T, wait = 300) {
  let last = 0;
  let timer: number | undefined;
  return (...args: Parameters<T>) => {
    const now = Date.now();
    if (now - last >= wait) {
      last = now;
      fn(...args);
      return;
    }
    window.clearTimeout(timer);
    timer = window.setTimeout(() => {
      last = Date.now();
      fn(...args);
    }, wait - (now - last));
  };
}

/**
 * 将样式对象转换为行内样式字符串。
 *
 * @param styles 样式对象。
 * @returns CSS 字符串。
 * @example
 * ```ts
 * const style = getCssObject({ color: "red", width: "100px" });
 *
 * // style => "color:red;width:100px"
 * ```
 */
export function getCssObject(styles: Record<string, string> = {}) {
  return Object.entries(styles)
    .map(([key, value]) => `${key}:${value}`)
    .join(";");
}

/**
 * 将数字格式化为中文紧凑计数。
 *
 * @param value 数字或数字字符串。
 * @returns 格式化后的统计数字。
 * @example
 * ```ts
 * const value = formatStatisticNumber(12000);
 *
 * // value => "1.2万"
 * ```
 */
export function formatStatisticNumber(value: number | string = 0) {
  const number = Number(value) || 0;
  return new Intl.NumberFormat("zh-CN", {
    notation: "compact",
    maximumFractionDigits: 1,
  }).format(number);
}

/**
 * 获取字符串的最后两个字符。
 *
 * @param name 输入字符串。
 * @returns 最后两个字符；长度小于等于 2 时返回原字符串。
 * @example
 * ```ts
 * const shortName = getLastTwoCharacters("管理员");
 *
 * // shortName => "理员"
 * ```
 */
export function getLastTwoCharacters(name: string): string {
  if (!name) return "";
  name = String(name);
  // 如果名字长度小于或等于2，直接返回名字
  if (name.length <= 2) {
    return name;
  }
  // 返回名字的最后两个字符
  return name.slice(-2);
}

/**
 * 获取url上的参数
 *
 * @returns 当前页面 query 参数对象。
 * @example
 * ```ts
 * // location.search = "?id=1&name=admin"
 * const query = getUrlQuery();
 *
 * // query => { id: "1", name: "admin" }
 * ```
 */
export function getUrlQuery() {
  const [, query] = location.search.split("?");
  if (!query) return {};
  const queryParams = query.split("&").reduce((p: Record<string, string>, c) => {
    const [key, value] = c.split("=");
    p[key] = value;
    return p;
  }, {});
  return queryParams;
}

/**
 * 把对象转成url参数，比如 "a=1&b=2"
 *
 * @param obj 参数对象。
 * @returns URL query 字符串。
 * @example
 * ```ts
 * const query = objToQueryString({ id: 1, name: "admin" });
 *
 * // query => "id=1&name=admin"
 * ```
 */
export function objToQueryString(obj: Record<string, any>): string {
  const queryParams = [];

  for (const key in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, key)) {
      const value = obj[key];
      const encodedKey = encodeURIComponent(key);
      const encodedValue = encodeURIComponent(value);
      queryParams.push(`${encodedKey}=${encodedValue}`);
    }
  }

  return queryParams.join("&");
}

/**
 * 获取列表排序序号  1 2 3 4 5...
 * @param index 索引值
 * @param pageNum 当前页码
 * @param pageSize 每页页数 默认10
 * @returns 跨分页后的序号。
 * @example
 * ```ts
 * const order = getOrdinalKey(0, 2, 10);
 *
 * // order => 11
 * ```
 */
export function getOrdinalKey(index = 0, pageNum = 1, pageSize = 10) {
  return pageSize * (pageNum - 1) + index + 1;
}


/**
 * 根据表达式取值
 * @param obj 对象
 * @param expression 表达式 aa.bb.cc
 * @returns 表达式对应的值；路径不存在时返回 `undefined`。
 * @example
 * ```ts
 * const value = getValueByExpression({ user: { name: "张三" } }, "user.name");
 *
 * // value => "张三"
 * ```
 */
export function getValueByExpression(obj: Record<string, any>, expression: string) {
  const keys = expression.split(".");
  let value = obj;
  for (let key of keys) {
    if (value && typeof value === "object" && key in value) {
      value = value[key];
    } else {
      return undefined;
    }
  }
  return value;
}

/**
 * 从对象中提取指定路径的值
 * @param obj 要提取值的源对象
 * @param pathString 路径字符串，例如 "a,b,c" 或 "a.b.c"
 * @returns 提取的值的对象
 * @example
 * ```ts
 * const value = extractValuesFromObject({ id: 1, name: "admin" }, "id,name");
 *
 * // value => { id: 1, name: "admin" }
 * ```
 */
export function extractValuesFromObject(obj: Record<string, any>, pathString: string) {
  // 处理空输入
  if (!obj || !pathString) {
    return {};
  }
  const separator = ",";
  const resObj: Record<string, any> = {};
  // 分割路径字符串
  const paths = pathString.split(separator).filter(Boolean);
  paths.forEach((key) => {
    resObj[key] = obj[key];
  });
  return resObj;
}

/**
 *
 * @param name 文件名称
 * @returns 文件相对路径
 * @example
 * ```ts
 * const url = getImageUrl("logo.png");
 *
 * // url => "/src/assets/images/logo.png" 对应的资源地址
 * ```
 */
export function getImageUrl(name: string) {
  return new URL(`/src/assets/images/${name}`, import.meta.url).href;
}

/**
 * 将时间范围数组拆成指定字段对象。
 *
 * @param rangerTime 时间范围数组。
 * @param keys 开始时间和结束时间对应的字段名。
 * @returns 拆分后的对象。
 * @example
 * ```ts
 * const value = rangeTimeFormat(["2026-01-01", "2026-01-31"], ["startTime", "endTime"]);
 *
 * // value => { startTime: "2026-01-01", endTime: "2026-01-31" }
 * ```
 */
export function rangeTimeFormat(rangerTime: string[], keys: string[]) {
  const [startTime, endTime] = keys || [];
  if (!rangerTime)
    return {
      [startTime]: "",
      [endTime]: "",
    };
  const [startTimeValue, endTimeValue] = rangerTime;

  return {
    [startTime]: startTimeValue,
    [endTime]: endTimeValue,
  };
}
