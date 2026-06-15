import dayjs, { type Dayjs } from "dayjs";

/**
 * 格式化日期时间。
 *
 * @param value 日期值，支持字符串、时间戳和 Date 对象。
 * @param format dayjs 格式模板，默认 `YYYY-MM-DD HH:mm:ss`。
 * @returns 格式化后的日期字符串；无效日期返回 `-`。
 * @example
 * ```ts
 * const value = formatDateTime("2026-01-01 08:30:00", "YYYY-MM-DD");
 *
 * // value => "2026-01-01"
 * ```
 */
export function formatDateTime(value?: string | number | globalThis.Date, format = "YYYY-MM-DD HH:mm:ss") {
  if (value === undefined || value === null || value === "") {
    return "-";
  }
  const result = dayjs(value);
  return result.isValid() ? result.format(format) : "-";
}

/**
 * 获取当前时间戳。
 *
 * @returns 当前毫秒时间戳。
 * @example
 * ```ts
 * const timestamp = nowTimestamp();
 *
 * // timestamp => 1779724800000
 * ```
 */
export function nowTimestamp() {
  return globalThis.Date.now();
}

/**
 * 获取一个月前的毫秒时间戳。
 *
 * @returns 当前时间往前一个月的毫秒时间戳。
 * @example
 * ```ts
 * const timestamp = previousMonthTimestamp();
 *
 * // timestamp => 当前时间减去一个月
 * ```
 */
export function previousMonthTimestamp() {
  return dayjs().subtract(1, "month").valueOf();
}

/**
 * 时间戳转日期
 * @param timestamp 时间戳
 * @param format 格式 默认Y-M-D h:m:s
 * @returns 格式化后的日期字符串。
 * @example
 * ```ts
 * const value = timestampToTime(1779724800000, "Y-M-D");
 *
 * // value => "2026-05-25"
 * ```
 */
export function timestampToTime(timestamp: number, format = "Y-M-D h:m:s"): string {
  const date = new Date(timestamp);
  const obj: Record<string, string | number> = {
    Y: date.getFullYear(),
    M: date.getMonth() + 1 < 10 ? `0${date.getMonth() + 1}` : date.getMonth() + 1,
    D: date.getDate() < 10 ? `0${date.getDate()}` : date.getDate(),
    h: date.getHours() < 10 ? `0${date.getHours()}` : date.getHours(),
    m: date.getMinutes() < 10 ? `0${date.getMinutes()}` : date.getMinutes(),
    s: date.getSeconds() < 10 ? `0${date.getSeconds()}` : date.getSeconds(),
  };
  const newitem = format.split("").map((item) => {
    for (let key in obj) {
      if (item === key) {
        item = obj[key].toString();
      }
    }
    return item;
  });
  return newitem.join("");
}

/**
 * 判断一个值是否是时间戳
 * @param value 时间戳
 * @returns 是有效时间戳时返回 `true`，否则返回 `false`。
 * @example
 * ```ts
 * const valid = isTimestamp(1779724800000);
 *
 * // valid => true
 * ```
 */
export function isTimestamp(value: unknown): boolean {
  if (isNaN(Number(value))) {
    return false;
  }
  const date = new Date(Number(value));
  return Number(value) === date.getTime();
}

/**
 * 日期转时间戳
 * @param date
 * @returns 秒级时间戳。
 * @example
 * ```ts
 * const timestamp = timesToStamp("2026-05-25");
 *
 * // timestamp => 1779667200
 * ```
 */
export function timesToStamp(date: string): number | null {
  return globalThis.Date.parse(`${+new Date(date)}`) / 1000;
}

/**
 * 转换日期为Moment对象
 * @param timestamp 时间戳
 * @returns Dayjs 对象；无时间戳时返回 `null`。
 * @example
 * ```ts
 * const date = getDateMoment(1779724800000);
 *
 * // date?.format("YYYY-MM-DD") => "2026-05-25"
 * ```
 */
export function getDateMoment(timestamp: number): Dayjs | null {
  if (!timestamp) {
    return null;
  }
  const value = timestampToTime(timestamp).replace(/\-/g, "/");

  return dayjs(new Date(value));
}

/**
 * 转换日期
 * @param timestamp 时间戳
 * @param format 格式
 * @returns 格式化后的日期字符串；无效值返回 `-`。
 * @example
 * ```ts
 * const value = getDateStr(1779724800000, "YYYY-MM-DD");
 *
 * // value => "2026-05-25"
 * ```
 */
export function getDateStr(timestamp: number, format = "YYYY-MM-DD HH:mm:ss"): string {
  const momentObj = getDateMoment(+timestamp);
  if (!momentObj) {
    return "-";
  }
  return momentObj.format(format);
}

/**
 * 把毫秒转成x天x小时格式
 * @param ms
 * @returns 易读的耗时字符串。
 * @example
 * ```ts
 * const value = formatMilliseconds(3661000);
 *
 * // value => "1小时1分钟1秒"
 * ```
 */
export function formatMilliseconds(ms: number): string {
  const seconds = Math.abs(ms / 1000);
  console.log({ seconds });
  if (isNaN(seconds)) {
    return "0分";
  }

  // 计算各个时间单位
  const days = Math.floor(seconds / 86400);
  const hours = Math.floor((seconds % 86400) / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const secs = Math.floor(seconds % 60);

  // 构建结果数组
  const parts: string[] = [];

  if (days > 0) {
    parts.push(`${days}天`);
    if (hours > 0) parts.push(`${hours}小时`);
    if (minutes > 0) parts.push(`${minutes}分钟`);
    if (secs > 0) parts.push(`${secs}秒`);
  } else if (hours > 0) {
    parts.push(`${hours}小时`);
    if (minutes > 0) parts.push(`${minutes}分钟`);
    if (secs > 0) parts.push(`${secs}秒`);
  } else if (minutes > 0) {
    parts.push(`${minutes}分钟`);
    if (secs > 0) parts.push(`${secs}秒`);
  }
  return parts.join("");
}

/**
 * 将秒数格式化为易读的时间字符串
 * @param seconds 总秒数
 * @returns 格式化后的时间字符串
 * @example
 * ```ts
 * const value = formatDuration(3661);
 *
 * // value => "1小时1分钟1秒"
 * ```
 */
export function formatDuration(seconds: number): string {
  console.log({ seconds });
  if (isNaN(seconds) || seconds < 0) {
    return "0秒";
  }

  // 计算各个时间单位
  const days = Math.floor(seconds / 86400);
  const hours = Math.floor((seconds % 86400) / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const secs = Math.floor(seconds % 60);

  // 构建结果数组
  const parts: string[] = [];

  if (days > 0) {
    parts.push(`${days}天`);
    if (hours > 0) parts.push(`${hours}小时`);
    if (minutes > 0) parts.push(`${minutes}分钟`);
    if (secs > 0) parts.push(`${secs}秒`);
  } else if (hours > 0) {
    parts.push(`${hours}小时`);
    if (minutes > 0) parts.push(`${minutes}分钟`);
    if (secs > 0) parts.push(`${secs}秒`);
  } else if (minutes > 0) {
    parts.push(`${minutes}分钟`);
    if (secs > 0) parts.push(`${secs}秒`);
  } else {
    parts.push(`${secs}秒`);
  }

  // 处理所有单位都为零的情况
  if (parts.length === 0) {
    return "0秒";
  }

  return parts.join("");
}

/**
 * 将毫秒数格式化为易读的时间字符串
 * @param milliseconds 总毫秒数
 * @returns 格式化后的时间字符串
 * @example
 * ```ts
 * const value = formatDurationFromMs(3661000);
 *
 * // value => "1小时1分钟1秒"
 * ```
 */
export function formatDurationFromMs(milliseconds: number): string {
  if (isNaN(milliseconds) || milliseconds < 0) {
    return "0秒";
  }

  // 转换为秒（四舍五入到整数）
  const totalSeconds = Math.round(milliseconds / 1000);

  // 计算各个时间单位
  const days = Math.floor(totalSeconds / 86400);
  const hours = Math.floor((totalSeconds % 86400) / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = Math.floor(totalSeconds % 60);

  // 构建结果数组
  const parts: string[] = [];

  if (days > 0) {
    parts.push(`${days}天`);
    if (hours > 0) parts.push(`${hours}小时`);
    if (minutes > 0) parts.push(`${minutes}分钟`);
    if (seconds > 0) parts.push(`${seconds}秒`);
  } else if (hours > 0) {
    parts.push(`${hours}小时`);
    if (minutes > 0) parts.push(`${minutes}分钟`);
    if (seconds > 0) parts.push(`${seconds}秒`);
  } else if (minutes > 0) {
    parts.push(`${minutes}分钟`);
    if (seconds > 0) parts.push(`${seconds}秒`);
  } else {
    parts.push(`${seconds}秒`);
  }

  return parts.length > 0 ? parts.join("") : "0秒";
}

/**
 * 获取耗时
 * @param givenTimeStr 给定时间字符串。
 * @returns 给定时间到当前时间的毫秒差。
 * @example
 * ```ts
 * const diff = getTimeDifference("2026-05-25 10:00:00");
 *
 * // diff => 当前时间减去给定时间的毫秒数
 * ```
 */
export function getTimeDifference(givenTimeStr: string): number {
  if (!givenTimeStr) return 0;
  // 将给定的时间字符串转换为 Date 对象
  const givenTime = new Date(givenTimeStr);

  // 获取当前时间的时间戳（毫秒）
  const currentTime = new Date().getTime();

  // 计算时间差（毫秒）
  const timeDiff = currentTime - givenTime.getTime();
  console.log({ timeDiff });
  // 返回时间差（秒）
  return timeDiff;
}
