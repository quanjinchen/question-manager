type OptionItem = {
  id: string | number;
  name: string;
  [key: string]: unknown;
};

/**
 * 根据指定字段和值从数组中移除第一条匹配项，会直接修改原数组。
 *
 * @param array 需要处理的数组。
 * @param key 用于匹配的字段名。
 * @param val 需要匹配的字段值。
 * @returns 被移除项原来的下标；未找到时返回 `-1`。
 * @example
 * ```ts
 * const list = [{ id: "1" }, { id: "2" }];
 * const index = removeByKey(list, "id", "1");
 *
 * // index => 0
 * // list => [{ id: "2" }]
 * ```
 */
export function removeByKey<T>(array: T[], key: keyof T, val: string): number {
  const index = array.findIndex((item) => item[key] === val);
  if (index > -1) {
    array.splice(index, 1);
  }
  return index;
}

/**
 * 判断数组中是否存在指定字段等于目标值的对象。
 *
 * @param array 需要查询的数组。
 * @param key 用于匹配的字段名。
 * @param val 需要匹配的字段值。
 * @returns 存在匹配项时返回 `true`，否则返回 `false`。
 * @example
 * ```ts
 * const list = [{ id: "1" }];
 * const exists = includesItemByKey(list, "id", "1");
 *
 * // exists => true
 * ```
 */
export function includesItemByKey<T>(array: T[], key: keyof T, val: string): boolean {
  const index = array.findIndex((item) => item[key] == val);
  return index != -1;
}

/**
 * 根据指定字段和值查找数组中的第一条匹配项。
 *
 * @param array 需要查询的数组。
 * @param key 用于匹配的字段名。
 * @param val 需要匹配的字段值。
 * @returns 找到时返回对应对象，未找到时返回 `undefined`。
 * @example
 * ```ts
 * const list = [{ id: "1", name: "管理员" }];
 * const item = findItemById(list, "id", "1");
 *
 * // item?.name => "管理员"
 * ```
 */
export function findItemById<T>(array: T[], key: keyof T, val: string): T | undefined {
  return array.find((item) => item[key] == val);
}

/**
 * 在标准选项数组中根据 id 查找选项对象。
 *
 * @param list 选项数组。
 * @param id 目标选项 id。
 * @returns 找到时返回选项对象，未找到时返回 `undefined`。
 * @example
 * ```ts
 * const item = findIdItem([{ id: 1, name: "启用" }], 1);
 *
 * // item?.name => "启用"
 * ```
 */
export function findIdItem<T extends OptionItem>(list: T[] = [], id: string | number): T | undefined {
  return list.find((item) => item.id === id);
}

/**
 * 在标准选项数组中根据 id 查找选项名称。
 *
 * @param list 选项数组。
 * @param id 目标选项 id。
 * @returns 找到时返回选项名称，未找到时返回 `-`。
 * @example
 * ```ts
 * const name = findIdName([{ id: "admin", name: "管理员" }], "admin");
 *
 * // name => "管理员"
 * ```
 */
export function findIdName<T extends OptionItem>(list: T[] = [], id: string | number): string {
  return findIdItem(list, id)?.name ?? "-";
}

/**
 * 按对象的指定字段对数组去重。
 *
 * @param arr 需要去重的数组。
 * @param key 去重依据的字段名。
 * @param keepLast 是否保留最后出现的对象，默认保留最后一条。
 * @returns 去重后的新数组。
 * @example
 * ```ts
 * const list = [{ id: 1, name: "A" }, { id: 1, name: "B" }];
 * const result = uniqueByKey(list, "id");
 *
 * // result => [{ id: 1, name: "B" }]
 * ```
 */
export function uniqueByKey<T extends Record<string, any>, K extends keyof T>(arr: T[], key: K, keepLast = true): T[] {
  if (!Array.isArray(arr) || arr.length === 0) return [];

  const map = new Map();

  arr.forEach((item) => {
    const value = item[key];
    // keepLast 为 true 时覆盖之前的值，否则只保留第一个
    if (keepLast || !map.has(value)) {
      map.set(value, item);
    }
  });

  return Array.from(map.values());
}
