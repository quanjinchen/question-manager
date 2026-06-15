import type { RequestResponse } from './types';

// 统一建模业务错误，方便后续在请求层和页面层区分“业务失败”和“网络失败”。
export class BusinessError<T = any> extends Error {
  code?: number | string;
  data?: T;
  isBusinessError = true;
  response?: RequestResponse<T>;
  status?: number;

  constructor(
    message: string,
    options: {
      code?: number | string;
      data?: T;
      response?: RequestResponse<T>;
      status?: number;
    } = {}
  ) {
    super(message);
    this.name = 'BusinessError';
    this.code = options.code;
    this.data = options.data;
    this.response = options.response;
    this.status = options.status;
  }
}

// 统一提供类型守卫，方便 catch 中做收敛判断。
export function isBusinessError(error: unknown): error is BusinessError {
  return Boolean(
    error &&
      typeof error === 'object' &&
      'isBusinessError' in error &&
      (error as BusinessError).isBusinessError
  );
}
