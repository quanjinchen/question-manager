import type { RequestClientOptions } from './types';

// 查询参数最终都要转成 query string，这里先把单个 key/value 的拼接逻辑收敛起来。
function appendPair(pairs: string[], key: string, value: unknown) {
  if (value === null || typeof value === 'undefined') {
    return;
  }

  const normalizedValue = value instanceof Date ? value.toISOString() : String(value);
  pairs.push(`${encodeURIComponent(key)}=${encodeURIComponent(normalizedValue)}`);
}

function buildPairs(
  pairs: string[],
  key: string,
  value: unknown,
  mode: 'brackets' | 'comma' | 'indices' | 'repeat'
) {
  // 统一处理数组、对象和基础类型，避免不同序列化模式下重复写分支。
  if (value === null || typeof value === 'undefined') {
    return;
  }

  if (Array.isArray(value)) {
    if (mode === 'comma') {
      appendPair(
        pairs,
        key,
        value
          .filter(item => item !== null && typeof item !== 'undefined')
          .map(item => (item instanceof Date ? item.toISOString() : String(item)))
          .join(',')
      );
      return;
    }

    value.forEach((item, index) => {
      if (mode === 'indices') {
        buildPairs(pairs, `${key}[${index}]`, item, mode);
        return;
      }

      if (mode === 'brackets') {
        buildPairs(pairs, `${key}[]`, item, mode);
        return;
      }

      buildPairs(pairs, key, item, mode);
    });
    return;
  }

  if (Object.prototype.toString.call(value) === '[object Object]') {
    Object.entries(value as Record<string, unknown>).forEach(([childKey, childValue]) => {
      buildPairs(pairs, `${key}[${childKey}]`, childValue, mode);
    });
    return;
  }

  appendPair(pairs, key, value);
}

function createParamsSerializer(mode: 'brackets' | 'comma' | 'indices' | 'repeat') {
  return (params: Record<string, unknown>) => {
    const pairs: string[] = [];
    Object.entries(params ?? {}).forEach(([key, value]) => {
      buildPairs(pairs, key, value, mode);
    });
    return pairs.join('&');
  };
}

/**
 * 把简写的参数序列化配置转换成 axios 可用的序列化函数。
 *
 * 输入示例：
 * - `repeat`
 * - `brackets`
 * - 自定义函数 `(params) => 'a=1&b=2'`
 *
 * 输出示例：
 * - 输入 `repeat` 且参数为 `{ ids: [1, 2] }`，输出函数会生成 `ids=1&ids=2`
 * - 输入 `comma` 且参数为 `{ ids: [1, 2] }`，输出函数会生成 `ids=1,2`
 * - 输入自定义函数时，原样返回该函数
 */
export function getParamsSerializer(paramsSerializer: RequestClientOptions['paramsSerializer']) {
  // 允许调用方用简短模式名声明数组序列化方式，内部再转成 axios 需要的函数。
  if (typeof paramsSerializer === 'string') {
    switch (paramsSerializer) {
      case 'brackets':
      case 'comma':
      case 'indices':
      case 'repeat': {
        return createParamsSerializer(paramsSerializer);
      }
      default: {
        return paramsSerializer;
      }
    }
  }

  return paramsSerializer;
}

/**
 * 按字段名或字段提取函数，从响应对象中取出目标值。
 *
 * 输入示例：
 * - `target = { code: 0, data: { id: 1 } }`, `field = 'data'`
 * - `target = { code: 0, result: { id: 1 } }`, `field = (res) => res.result`
 *
 * 输出示例：
 * - 上例 1 输出 `{ id: 1 }`
 * - 上例 2 输出 `{ id: 1 }`
 * - `field` 为空时，直接返回原始 `target`
 */
export function readValue(target: any, field: ((response: any) => any) | string | undefined) {
  if (typeof field === 'function') {
    return field(target);
  }

  if (!field) {
    return target;
  }

  return target?.[field];
}

/**
 * 安全拼接 baseURL 和相对路径，兼容绝对地址、相对地址和纯路径前缀。
 *
 * 输入示例：
 * - `baseUrl = 'https://api.example.com/'`, `url = '/users'`
 * - `baseUrl = '/api'`, `url = 'users'`
 * - `baseUrl = 'https://api.example.com'`, `url = 'https://other.example.com/a'`
 *
 * 输出示例：
 * - 上例 1 输出 `https://api.example.com/users`
 * - 上例 2 输出 `/api/users`
 * - 上例 3 输出 `https://other.example.com/a`
 */
export function safeJoinUrl(baseUrl: string | undefined, url: string) {
  if (!baseUrl) {
    return url;
  }

  if (/^https?:\/\//i.test(url)) {
    return url;
  }

  if (/^https?:\/\//i.test(baseUrl)) {
    return new URL(url, baseUrl).toString();
  }

  return `${baseUrl.replace(/\/+$/, '')}/${url.replace(/^\/+/, '')}`;
}

/**
 * 判断一个值是否为 `undefined`，主要给上传场景过滤空字段。
 *
 * 输入示例：
 * - `undefined`
 * - `null`
 * - `0`
 *
 * 输出示例：
 * - 输入 `undefined` 输出 `true`
 * - 输入 `null` 输出 `false`
 * - 输入 `0` 输出 `false`
 */
export function isUndefined(value: unknown): value is undefined {
  return typeof value === 'undefined';
}
