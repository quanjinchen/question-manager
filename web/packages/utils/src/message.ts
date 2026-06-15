import { ElMessage, ElMessageBox } from 'element-plus';

/**
 * 显示 Element Plus 成功消息。
 *
 * @param options 传给 `ElMessage` 的配置项，会覆盖默认成功提示配置。
 * @example
 * ```ts
 * messageAlert({ message: "保存成功" });
 * ```
 */
export function messageAlert(options: Record<string, any> = {}) {
  ElMessage({
    type: 'success',
    message: '操作成功',
    duration: 2000,
    showClose: false,
    ...options
  });
}

/**
 * 显示确认弹窗。
 *
 * @param message 确认文案，默认是 `确认执行当前操作吗？`。
 * @param title 弹窗标题，默认是 `操作确认`。
 * @param options 传给 `ElMessageBox.confirm` 的配置项。
 * @returns 用户确认时 resolve，取消或关闭时 reject 的 Promise。
 * @example
 * ```ts
 * await messageConfirm("确认删除该用户吗？");
 *
 * // 用户点击确认后继续执行
 * ```
 */
export function messageConfirm(message = '确认执行当前操作吗？', title = '操作确认', options = {}) {
  return ElMessageBox.confirm(message, title, {
    type: 'warning',
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    ...options
  });
}

