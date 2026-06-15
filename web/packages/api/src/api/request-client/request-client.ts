import type { AxiosRequestConfig, AxiosResponse } from 'axios';
import type { RequestClientConfig, RequestClientOptions } from './types';
import axios from 'axios';
import { FileDownloader } from './modules/downloader';
import { InterceptorManager } from './modules/interceptor';
import { SSE } from './modules/sse';
import { FileUploader } from './modules/uploader';
import { getParamsSerializer } from './utils';

export class RequestClient {
  addRequestInterceptor: InterceptorManager['addRequestInterceptor'];

  addResponseInterceptor: InterceptorManager['addResponseInterceptor'];

  download: FileDownloader['download'];

  readonly instance;

  postSSE: SSE['postSSE'];

  requestSSE: SSE['requestSSE'];

  upload: FileUploader['upload'];

  constructor(options: RequestClientOptions = {}) {
    const defaultConfig: RequestClientOptions = {
      headers: {
        'Content-Type': 'application/json;charset=utf-8'
      },
      // 默认保留原始响应，真正返回什么交给响应拦截器统一决定。
      responseReturn: 'raw',
      timeout: 10000
    };

    const requestConfig: RequestClientOptions = {
      ...defaultConfig,
      ...options,
      headers: {
        ...(defaultConfig.headers ?? {}),
        ...(options.headers ?? {})
      }
    };

    requestConfig.paramsSerializer = getParamsSerializer(requestConfig.paramsSerializer);
    this.instance = axios.create(requestConfig);

    // 把上传、下载、SSE 都挂在同一个 client 上，后续扩展能力时不需要再散落到业务层。
    const interceptorManager = new InterceptorManager(this.instance);
    this.addRequestInterceptor = interceptorManager.addRequestInterceptor.bind(interceptorManager);
    this.addResponseInterceptor = interceptorManager.addResponseInterceptor.bind(interceptorManager);

    const uploader = new FileUploader(this);
    this.upload = uploader.upload.bind(uploader);

    const downloader = new FileDownloader(this);
    this.download = downloader.download.bind(downloader);

    const sse = new SSE(this);
    this.postSSE = sse.postSSE.bind(sse);
    this.requestSSE = sse.requestSSE.bind(sse);
  }

  /**
   * 发起 DELETE 请求。
   *
   * 输入示例：
   * - `url = '/api/user/1'`
   * - `config = { responseReturn: 'body' }`
   *
   * 输出示例：
   * - 接口成功时，返回值取决于 `responseReturn`
   * - `responseReturn = 'raw'` 时返回完整 axios 响应
   * - `responseReturn = 'body'` 时返回响应体
   */
  delete<T = any>(url: string, config?: RequestClientConfig): Promise<T> {
    return this.request<T>(url, { ...config, method: 'DELETE' });
  }

  /**
   * 发起 GET 请求。
   *
   * 输入示例：
   * - `url = '/api/user/list'`
   * - `config = { params: { pageNum: 1, pageSize: 10 } }`
   *
   * 输出示例：
   * - 若后端返回 `{ code: 0, data: [...] }` 且响应拦截器配置为 `data`
   *   则最终返回 `[...]`
   */
  get<T = any>(url: string, config?: RequestClientConfig): Promise<T> {
    return this.request<T>(url, { ...config, method: 'GET' });
  }

  /**
   * 读取当前 client 的 baseURL。
   *
   * 输入示例：
   * - 无入参
   *
   * 输出示例：
   * - 若当前 client 配置了 `https://api.example.com`，则返回该字符串
   * - 若未配置，则返回 `undefined`
   */
  getBaseUrl() {
    // SSE 等不走 axios.response 的场景也需要 baseURL，因此单独暴露一个读取方法。
    return this.instance.defaults.baseURL;
  }

  /**
   * 发起 PATCH 请求。
   *
   * 输入示例：
   * - `url = '/api/user/1'`
   * - `data = { nickname: 'Tom' }`
   *
   * 输出示例：
   * - 成功时返回值同样取决于响应拦截器解包策略
   */
  patch<T = any>(url: string, data?: any, config?: RequestClientConfig): Promise<T> {
    return this.request<T>(url, { ...config, data, method: 'PATCH' });
  }

  /**
   * 发起 POST 请求。
   *
   * 输入示例：
   * - `url = '/api/user/create'`
   * - `data = { username: 'admin' }`
   *
   * 输出示例：
   * - 若接口返回 `{ code: 0, data: { id: 1 } }`
   *   且响应配置为 `data`，则输出 `{ id: 1 }`
   */
  post<T = any>(url: string, data?: any, config?: RequestClientConfig): Promise<T> {
    return this.request<T>(url, { ...config, data, method: 'POST' });
  }

  /**
   * 发起 PUT 请求。
   *
   * 输入示例：
   * - `url = '/api/user/update'`
   * - `data = { id: 1, username: 'root' }`
   *
   * 输出示例：
   * - 成功时返回值同样由 `responseReturn` 和响应拦截器决定
   */
  put<T = any>(url: string, data?: any, config?: RequestClientConfig): Promise<T> {
    return this.request<T>(url, { ...config, data, method: 'PUT' });
  }

  /**
   * 发起底层通用请求，是当前请求客户端的统一出口。
   *
   * 输入示例：
   * - `url = '/api/user/list'`
   * - `config = { method: 'GET', params: { pageNum: 1 }, responseReturn: 'body' }`
   *
   * 输出示例：
   * - 成功时返回 axios 响应、响应体或解包后的 data，取决于响应拦截器配置
   * - 业务码失败时会抛出 `BusinessError`
   * - 网络失败时会抛出 axios 或网络错误
   */
  async request<T>(url: string, config: RequestClientConfig = {}): Promise<T> {
    const finalConfig: RequestClientConfig = {
      ...config,
      paramsSerializer: getParamsSerializer(config.paramsSerializer),
      url
    };

    // 所有请求都走同一个实例，拦截器、共享状态和扩展能力才能稳定复用。
    const response: AxiosResponse<T> = await this.instance(finalConfig as AxiosRequestConfig);
    return response as T;
  }
}
