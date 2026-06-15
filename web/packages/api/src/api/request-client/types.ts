import type {
  AxiosRequestConfig,
  AxiosResponse,
  CreateAxiosDefaults,
  InternalAxiosRequestConfig
} from 'axios';

// 数组查询参数常见有多种序列化方式，不同后端偏好不同，这里先统一成几个常用模式。
export type ParamsSerializerMode = 'brackets' | 'comma' | 'indices' | 'repeat';

// requestOptions 是当前脚手架额外附加在请求上的元信息，不直接传给后端。
export type HttpRequestMeta = {
  alertError?: boolean;
  needLogin?: boolean;
  paramsKey?: 'params' | 'data';
  skipBusinessError?: boolean;
};

// 这些扩展选项统一挂在 RequestClientConfig 上，方便拦截器在同一个地方读取。
type ExtendOptions<T = any> = {
  // 约定查询参数的序列化方式，主要解决数组参数如何拼成 query string 的问题。
  paramsSerializer?: ParamsSerializerMode | AxiosRequestConfig<T>['paramsSerializer'];
  // 控制请求最终返回什么：原始响应、整个响应体，还是解包后的 data。
  responseReturn?: 'raw' | 'body' | 'data';
  // 允许按项目后端协议自定义业务状态码字段名，默认通常是 code。
  codeField?: string;
  // 指定业务数据字段名，或传入函数自己决定如何从响应体里取值。
  dataField?: ((response: any) => any) | string;
  // 指定后端错误消息字段名，业务失败时优先从这里读取提示文案。
  messageField?: string;
  // 指定业务成功码，支持常量值或判断函数，方便适配不同后端协议。
  successCode?: ((code: any) => boolean) | number | string;
  // 当前脚手架附加在请求上的元信息，给请求/响应拦截器消费。
  requestOptions?: HttpRequestMeta;
};

export type RequestClientConfig<T = any> = AxiosRequestConfig<T> & ExtendOptions<T>;

// 响应对象额外补上扩展 config 类型，便于在响应拦截器里访问自定义配置。
export type RequestResponse<T = any> = AxiosResponse<T> & {
  config: InternalAxiosRequestConfig<T> & ExtendOptions<T>;
};

// 常见 content-type 枚举出来，后续如果要做类型约束或快捷方法会更方便。
export type RequestContentType =
  | 'application/json;charset=utf-8'
  | 'application/octet-stream;charset=utf-8'
  | 'application/x-www-form-urlencoded;charset=utf-8'
  | 'multipart/form-data;charset=utf-8';

export type RequestClientOptions = CreateAxiosDefaults & ExtendOptions;

// SSE 除了原生 RequestInit，还补充 onMessage / onEnd 两个高频回调。
export interface SseRequestOptions extends RequestInit {
  onEnd?: () => void;
  onMessage?: (message: string) => void;
}

// 下载场景通常只关心 blob 或原始响应，因此单独抽一层配置类型。
export type DownloadRequestConfig = {
  responseReturn?: 'body' | 'raw';
} & Omit<RequestClientConfig, 'responseReturn'>;

// 请求拦截器和响应拦截器类型都提前统一，避免后续零散写 any。
export type RequestInterceptorConfig = {
  fulfilled?: (
    config: InternalAxiosRequestConfig<any> & ExtendOptions
  ) =>
    | (InternalAxiosRequestConfig<any> & ExtendOptions)
    | Promise<InternalAxiosRequestConfig<any> & ExtendOptions>;
  rejected?: (error: any) => any;
};

export type ResponseInterceptorConfig<T = any> = {
  fulfilled?: (
    response: RequestResponse<T>
  ) => Promise<RequestResponse | T> | RequestResponse | T;
  rejected?: (error: any) => any;
};

export type MakeErrorMessageFn = (message: string, error: any) => void;

// 约定当前脚手架最常见的后端响应结构，方便后续需要时做泛型约束。
export interface HttpResponse<T = any> {
  code: number;
  data: T;
  message: string;
}
