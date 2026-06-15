import type { AxiosInstance, AxiosResponse } from 'axios';
import type { RequestInterceptorConfig, ResponseInterceptorConfig } from '../types';

// 提供默认空实现，避免外部注册拦截器时反复写样板函数。
const defaultRequestInterceptorConfig: RequestInterceptorConfig = {
  fulfilled: config => config,
  rejected: error => Promise.reject(error)
};

const defaultResponseInterceptorConfig: ResponseInterceptorConfig = {
  fulfilled: (response: AxiosResponse) => response,
  rejected: error => Promise.reject(error)
};

export class InterceptorManager {
  constructor(private readonly axiosInstance: AxiosInstance) {}

  addRequestInterceptor({
    fulfilled,
    rejected
  }: RequestInterceptorConfig = defaultRequestInterceptorConfig) {
    // 请求拦截器统一从这里接入，保证 RequestClient 外部只暴露简洁 API。
    this.axiosInstance.interceptors.request.use(fulfilled, rejected);
  }

  addResponseInterceptor<T = any>({
    fulfilled,
    rejected
  }: ResponseInterceptorConfig<T> = defaultResponseInterceptorConfig) {
    // 响应拦截器允许返回解包后的数据，因此这里放宽成 any 交给上层约束。
    this.axiosInstance.interceptors.response.use(fulfilled as any, rejected);
  }
}
