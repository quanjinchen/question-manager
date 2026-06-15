import type { AxiosRequestHeaders, InternalAxiosRequestConfig } from 'axios';
import type { RequestClient } from '../request-client';
import type { SseRequestOptions } from '../types';
import { safeJoinUrl } from '../utils';

export class SSE {
  constructor(private readonly client: RequestClient) {}

  postSSE(url: string, data?: any, requestOptions?: SseRequestOptions) {
    return this.requestSSE(url, data, {
      ...requestOptions,
      method: 'POST'
    });
  }

  async requestSSE(url: string, data?: any, requestOptions?: SseRequestOptions) {
    const baseUrl = this.client.getBaseUrl() || '';

    // 先手动执行已有请求拦截器，确保 SSE 也能复用 token、baseURL 等公共注入逻辑。
    let axiosConfig: InternalAxiosRequestConfig<any> = {
      headers: {} as AxiosRequestHeaders,
      method: (requestOptions?.method as any) ?? 'GET',
      url
    };

    const requestInterceptors = this.client.instance.interceptors.request as any;
    if (requestInterceptors.handlers?.length) {
      for (const handler of requestInterceptors.handlers) {
        if (typeof handler?.fulfilled === 'function') {
          const next = await handler.fulfilled(axiosConfig as any);
          if (next) {
            axiosConfig = next as InternalAxiosRequestConfig<any>;
          }
        }
      }
    }

    const headers = new Headers();
    Object.entries((axiosConfig.headers ?? {}) as Record<string, string>).forEach(([key, value]) => {
      headers.set(key, String(value));
    });

    if (requestOptions?.headers) {
      new Headers(requestOptions.headers).forEach((value, key) => headers.set(key, value));
    }

    if (!headers.has('accept')) {
      headers.set('accept', 'text/event-stream');
    }

    let body = requestOptions?.body ?? data;
    // JSON body 统一在这里序列化，和普通请求的调用体验保持一致。
    const contentType = (headers.get('content-type') || '').toLowerCase();
    if (
      body &&
      typeof body === 'object' &&
      !ArrayBuffer.isView(body as any) &&
      !(body instanceof ArrayBuffer) &&
      !(body instanceof Blob) &&
      !(body instanceof FormData) &&
      contentType.includes('application/json')
    ) {
      body = JSON.stringify(body);
    }

    const response = await fetch(safeJoinUrl(baseUrl, url), {
      ...requestOptions,
      body,
      headers,
      method: axiosConfig.method
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const reader = response.body?.getReader();
    if (!reader) {
      throw new Error('No reader');
    }

    const decoder = new TextDecoder();

    // SSE 按流逐段读取，把每一段内容即时交给 onMessage 回调。
    while (true) {
      const { done, value } = await reader.read();
      if (done) {
        decoder.decode(new Uint8Array(0), { stream: false });
        requestOptions?.onEnd?.();
        reader.releaseLock?.();
        break;
      }

      requestOptions?.onMessage?.(decoder.decode(value, { stream: true }));
    }
  }
}
