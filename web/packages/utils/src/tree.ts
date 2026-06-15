type TreeConfig = {
  children?: string;
  additional?: Record<string, any>;
};

type TreeKeyConfig = {
  key?: string;
  children?: string;
};

/**
 * 遍历树结构，并对每个节点执行回调。
 *
 * @param treeData 树数据，可以是单个根节点，也可以是节点数组。
 * @param callback 每个节点都会执行的回调，第二个参数为透传的 additional。
 * @param config.children 子节点字段名，默认是 `list`。
 * @param config.additional 透传给 callback 的附加数据。
 * @example
 * ```ts
 * const tree = [{ id: 1, list: [{ id: 2 }] }];
 * const ids: number[] = [];
 *
 * forEachTree(tree, item => ids.push(item.id));
 *
 * // ids => [1, 2]
 * ```
 */
export function forEachTree(
  treeData: any,
  callback: (item: Record<string, any>, additional: Record<string, any>) => void,
  config?: TreeConfig,
) {
  if (!treeData) return;
  const { children = "list", additional = {} } = config || {};

  const loop = (data: Record<string, any>, additional: Record<string, any>) => {
    callback?.(data, additional);
    if (data[children] && data[children].length) {
      data[children].forEach((item: Record<string, any>) => loop(item, additional));
    }
  };

  const loopList = (array: Record<string, any>[], additional: Record<string, any>) => {
    if (array.length === 0) return;
    array.forEach((item) => {
      callback?.(item, additional);
      if (item[children] && item[children].length) {
        loopList(item[children], additional);
      }
    });
  };

  Array.isArray(treeData) ? loopList(treeData, additional) : loop(treeData, additional);
}

/**
 * 根据树节点 id 生成节点映射表。
 *
 * @param treeData 树数据，可以是单个根节点，也可以是节点数组。
 * @param config.key 节点唯一键字段名，默认是 `id`。
 * @param config.children 子节点字段名，默认是 `children`。
 * @returns 以节点唯一键为 key、节点对象为 value 的映射表。
 * @example
 * ```ts
 * const tree = [{ id: 1, children: [{ id: 2, name: "菜单" }] }];
 * const map = getTreeMap(tree);
 *
 * // map[2].name => "菜单"
 * ```
 */
export function getTreeMap(
  treeData: any,
  config?: TreeKeyConfig,
) {
  const { key = "id", children = "children" } = config || {};
  const treeMap: Record<string, any> = {};
  forEachTree(
    treeData,
    (item: Record<string, any>) => {
      treeMap[item[key]] = item;
    },
    {
      children,
      additional: {},
    },
  );
  return treeMap;
}

/**
 * 获取目标节点在树中的父子链路。
 *
 * @param param.treeData 树数据，通常是根节点数组。
 * @param param.id 目标节点 id。
 * @param param.config.key 节点唯一键字段名，默认是 `id`。
 * @param param.config.children 子节点字段名，默认是 `children`。
 * @returns 从根节点到目标节点的节点数组；未找到时返回 `undefined`。
 * @example
 * ```ts
 * const tree = [{ id: 1, children: [{ id: 2 }] }];
 * const link = getTreeLink({ treeData: tree, id: 2 });
 *
 * // link?.map(item => item.id) => [1, 2]
 * ```
 */
export function getTreeLink(param: {
  treeData: Record<string, any>[];
  id: number | string;
  config?: TreeKeyConfig;
}) {
  const { treeData, id, config } = param;
  const { key = "id", children = "children" } = config || {};

  function findPath(root: Record<string, any>[], targetId: string | number) {
    function findPathHelper(
      node: Record<string, any> | null | undefined,
      targetId: string | number,
      path: Record<string, any>[],
    ): Record<string, any>[] | null {
      if (!node) return null;

      if (node[key] === targetId) {
        path.push(node);
        return path;
      }

      for (const child of node?.[children] || []) {
        const newPath = findPathHelper(child, targetId, [...path, node]);
        if (newPath) return newPath;
      }

      return null;
    }

    for (const node of root) {
      const path = findPathHelper(node, targetId, []);
      if (path) return path;
    }
  }

  return findPath(treeData, id);
}

/**
 * 按条件过滤树节点。
 *
 * @param tree 树节点数组。
 * @param callback 节点过滤函数，返回 `true` 时保留当前节点。
 * @returns 过滤后的树节点数组。
 * @example
 * ```ts
 * const tree = [{ id: 1, enabled: true, children: [{ id: 2, enabled: false }] }];
 * const result = filterTree(tree, item => item.enabled);
 *
 * // result => [{ id: 1, enabled: true, children: [] }]
 * ```
 */
export function filterTree(tree: Record<string, any>[], callback: (node: Record<string, any>) => boolean) {
  function filterNode(
    node: Record<string, any> | null | undefined,
    callback: (node: Record<string, any>) => boolean,
  ): Record<string, any> | null {
    if (!node) return null;

    if (callback(node)) {
      const filteredChildren = node.children?.map((child: Record<string, any>) => filterNode(child, callback)).filter(Boolean);
      return { id: node.id, ...node, children: filteredChildren };
    }
    return null;
  }

  return tree.map((node: Record<string, any>) => filterNode(node, callback)).filter(Boolean);
}

/**
 * 遍历流程树中的有效流程节点。
 *
 * @param process 流程树根节点。
 * @param cb 遍历到有效流程节点时执行的回调。
 * @example
 * ```ts
 * const process = { type: "ROOT", id: 1, children: { type: "TASK", id: 2 } };
 * const ids: number[] = [];
 *
 * deepProcess(process, item => ids.push(item.id));
 *
 * // ids => [1, 2]
 * ```
 */
export function deepProcess(process: Record<string, any>, cb: (item: Record<string, any>) => void) {
  if (process?.type) {
    if (["ROOT", "APPROVAL", "TASK", "CC"].includes(process?.type)) {
      cb?.(process);
    }
    if (process.children) {
      deepProcess(process.children, cb);
    }
    if (process.branchs?.length) {
      process.branchs.forEach((branch: Record<string, any>) => {
        if (branch.children) deepProcess(branch.children, cb);
      });
    }
  }
}

/**
 * 在流程树中查找指定 id 的流程节点。
 *
 * @param process 流程树根节点。
 * @param id 目标流程节点 id。
 * @returns 匹配的流程节点；未找到时返回 `null`。
 * @example
 * ```ts
 * const process = { type: "ROOT", id: 1, children: { type: "TASK", id: 2 } };
 * const item = deepProcessFindItem(process, 2);
 *
 * // item?.type => "TASK"
 * ```
 */
export function deepProcessFindItem(process: Record<string, any>, id: string | number) {
  let res: Record<string, any> | null = null;
  deepProcess(process, (item: Record<string, any>) => {
    if (item.id == id && !res) {
      res = item;
    }
  });
  return res;
}
