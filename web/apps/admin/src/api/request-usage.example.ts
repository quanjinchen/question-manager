import { ElMessage } from 'element-plus';
import { exampleRequests } from './example-requests';

/**
 * 这个文件演示“业务层如何调用接口层”。
 *
 * 分层说明：
 * - `app-request.ts`：直接在业务侧处理 token、401 和错误提示
 * - `example-requests.ts`：接口层，统一描述业务请求
 * - 当前文件：业务调用层，只调用接口层，不直接碰底层请求客户端
 */

/**
 * 示例 1：业务层调用“用户详情接口”。
 *
 * 输入示例：
 * - 业务层传入 `{ id: 1001 }`
 *
 * 输出示例：
 * - 若后端返回 `{ code: 0, data: { id: 1001, username: 'admin' } }`
 *   则最终拿到 `{ id: 1001, username: 'admin' }`
 * - 若业务失败或网络失败，直接抛错进入 `catch`
 */
export async function loadUserDetailExample() {
  try {
    const result = await exampleRequests.users.detail({ id: 1001 });
    console.log('user detail result:', result);
    return result;
  } catch (error) {
    console.error('load user detail failed:', error);
    throw error;
  }
}

/**
 * 示例 2：业务层调用“创建用户接口”。
 *
 * 输入示例：
 * - 业务层传入 `{ username: 'new-user', password: '123456' }`
 *
 * 输出示例：
 * - 若接口成功，返回创建结果
 * - 若后端返回业务错误，例如“用户名已存在”，会直接抛错
 */
export async function createUserExample() {
  const result = await exampleRequests.users.create({
    username: 'new-user',
    password: '123456'
  });

  console.log('create user result:', result);
  return result;
}

/**
 * 示例 3：业务层调用“静默失败的分页接口”。
 *
 * 输入示例：
 * - 业务层传入分页参数 `{ pageNum: 1, pageSize: 10 }`
 *
 * 输出示例：
 * - 接口成功时返回分页结果
 * - 接口失败时仍然抛错，但因为接口层关闭了 `alertError`，不会触发统一错误提示
 */
export async function silentPageExample() {
  const result = await exampleRequests.operationLogs.pageSilently({
    pageNum: 1,
    pageSize: 10
  });

  console.log('silent page result:', result);
  return result;
}

/**
 * 示例 4：业务层调用“用户列表接口”。
 *
 * 输入示例：
 * - 业务层传入 `{ pageNum: 1, pageSize: 10 }`
 *
 * 输出示例：
 * - 若接口返回 `{ code: 0, data: [...] }`
 *   则最终拿到列表数据 `[...]`
 * - 若权限不通过，返回 `undefined`
 */
export async function listUsersExample() {
  const result = await exampleRequests.users.list({
    pageNum: 1,
    pageSize: 10
  });

  console.log('user list result:', result);
  return result;
}
