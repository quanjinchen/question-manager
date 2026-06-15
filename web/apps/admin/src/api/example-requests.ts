import { appRequest, type AppRequestMethodOptions } from '@/api/app-request';

/**
 * 这个文件演示“接口层如何基于 http 封装业务请求”。
 */
export const exampleRequests = {
  users: {
    list: (params: Record<string, any> = {}, options: AppRequestMethodOptions = {}) =>
      appRequest.post('/api/user/list-user', params, options),
    detail: (params: Record<string, any> = {}, options: AppRequestMethodOptions = {}) =>
      appRequest.get('/api/user/get-user-by-id', params, {
        appendPathOnGet: true,
        ...options
      }),
    create: (params: Record<string, any> = {}, options: AppRequestMethodOptions = {}) =>
      appRequest.post('/api/user/create-user', params, options)
  },
  operationLogs: {
    pageSilently: (params: Record<string, any> = {}, options: AppRequestMethodOptions = {}) =>
      appRequest.post('/api/operation-log/list-operation-log', params, {
        alertError: false,
        ...options
      })
  },
  faceAuthLogs: {
    pageSilently: (params: Record<string, any> = {}, options: AppRequestMethodOptions = {}) =>
      appRequest.post('/api/face-auth-log/list-face-auth-log', params, {
        alertError: false,
        ...options
      })
  }
};
