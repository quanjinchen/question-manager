import type { RequestClient } from '../request-client';
import type { RequestClientConfig } from '../types';
import { isUndefined } from '../utils';

export class FileUploader {
  constructor(private readonly client: RequestClient) {}

  async upload<T = any>(
    url: string,
    data: Record<string, any> & { file: Blob | File },
    config?: RequestClientConfig
  ): Promise<T> {
    const formData = new FormData();

    // 上传时把普通对象统一转成 FormData，数组字段也按照后端常见格式展开。
    Object.entries(data).forEach(([key, value]) => {
      if (Array.isArray(value)) {
        value.forEach((item, index) => {
          if (!isUndefined(item)) {
            formData.append(`${key}[${index}]`, item);
          }
        });
        return;
      }

      if (!isUndefined(value)) {
        formData.append(key, value);
      }
    });

    // multipart/form-data 统一在这里注入，业务层无需重复设置请求头。
    return this.client.post(url, formData, {
      ...config,
      headers: {
        'Content-Type': 'multipart/form-data',
        ...(config?.headers ?? {})
      }
    });
  }
}
