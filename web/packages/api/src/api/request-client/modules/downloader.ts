import type { RequestClient } from '../request-client';
import type { DownloadRequestConfig } from '../types';

export class FileDownloader {
  constructor(private readonly client: RequestClient) {}

  async download<T = Blob>(url: string, config?: DownloadRequestConfig): Promise<T> {
    const finalConfig: DownloadRequestConfig = {
      method: 'GET',
      responseReturn: 'body',
      responseType: 'blob',
      ...config,
      requestOptions: {
        // 文件下载通常没有统一业务码包裹，默认跳过 code/message 判断。
        skipBusinessError: true,
        ...(config?.requestOptions ?? {})
      }
    };

    return this.client.request<T>(url, finalConfig);
  }
}
