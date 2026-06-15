import { RequestClient } from '@vue-scaffold/api';
import { generateUuid } from '@vue-scaffold/utils';
import { showFailToast, showSuccessToast } from 'vant';
import { getCurrentAuthToken } from '@/apis/auth-session';

export type RequestMethod = 'get' | 'post' | 'put' | 'delete' | 'patch';

export type AppRequestCustomOptions = {
  paramsKey?: 'params' | 'data';
  alertSuccess?: boolean;
  alertError?: boolean;
  appendPathOnGet?: boolean;
  skipBusinessError?: boolean;
};

export type AppRequestMethodOptions = AppRequestCustomOptions & {
  axiosOptions?: Record<string, any> & {
    headers?: Record<string, any>;
  };
};

export type AppRequestUploadData = Record<string, any> & {
  file: Blob | File;
};

export type AppRequestDownloadOptions = AppRequestMethodOptions & {
  responseReturn?: 'body' | 'raw';
};

export type AppBusinessError = Error & {
  responseBody?: any;
  code?: number | string;
};

const requestBaseUrl = (window.location.origin).replace(/\/+$/, '');
const requestClient = createRequestClient();

function createRequestClient() {
  return new RequestClient({
    baseURL: requestBaseUrl,
    responseReturn: 'body',
    timeout: 30000
  });
}

function resolveBusinessMessage(response: any) {
  return response?.message ?? response?.msg;
}

function resolveBusinessCode(response: any) {
  return response?.code ?? response?.retCode;
}

function successHandler(message: string) {
  return showSuccessToast(message || '操作成功');
}

function errorHandler(message: string) {
  return showFailToast(message || '未知错误，请稍后重试');
}

function unwrapResponseBody(response: any) {
  return response && typeof response === 'object' && 'data' in response
    ? response.data
    : response;
}

function buildRequestHeaders(
  customHeaders: Record<string, any> = {},
  defaultContentType = 'application/json'
) {
  const token = getCurrentAuthToken();
  return {
    ...(defaultContentType
      ? {
        'Content-Type': defaultContentType
      }
      : {}),
    'X-REQUEST-ID': generateUuid(),
    'X-TIMESTAMP': String(Date.now()),
    ...(token
      ? {
        Authorization: token
      }
      : {}),
    ...customHeaders
  };
}

function buildBusinessError(responseBody: any, response?: any): AppBusinessError {
  const error = new Error(resolveBusinessMessage(responseBody) || '请求失败') as AppBusinessError;
  error.responseBody = responseBody;
  error.code = resolveBusinessCode(responseBody);
  (error as any).response = response;
  return error;
}

function resolveRequestResult(response: any, options: AppRequestCustomOptions = {}) {
  const {
    alertSuccess = false,
    skipBusinessError = false
  } = options;

  const responseData = unwrapResponseBody(response);
  const businessCode = resolveBusinessCode(responseData);
  const businessMessage = resolveBusinessMessage(responseData);

  if (!skipBusinessError && businessCode !== undefined && businessCode !== 0) {
    throw buildBusinessError(responseData, response);
  }

  if (alertSuccess) {
    successHandler(businessMessage || '操作成功');
  }

  if (responseData && typeof responseData === 'object' && 'data' in responseData) {
    return responseData.data;
  }

  return responseData;
}

function handleRequestError(error: any, alertError = true) {
  const response = error?.response;
  const responseBody = error?.responseBody ?? unwrapResponseBody(response);
  const businessMessage = resolveBusinessMessage(responseBody);

  if (!alertError) {
    return;
  }

  if (responseBody) {
    errorHandler(businessMessage || '请求失败');
    return;
  }

  if (error?.message?.includes?.('timeout')) {
    errorHandler('请求超时');
    return;
  }

  if (error?.request) {
    errorHandler('网络错误，请检查网络连接');
    return;
  }

  errorHandler(error?.message || '未知错误，请稍后重试');
}

function buildPathUrl(url: string, params: Record<string, any>) {
  const nextParams = { ...params };
  const pathValue = nextParams.id ?? Object.values(nextParams)[0];
  if (pathValue === undefined || pathValue === null || pathValue === '') {
    return { url, params: nextParams };
  }
  if ('id' in nextParams) {
    delete nextParams.id;
  }
  return {
    url: `${url.replace(/\/$/, '')}/${pathValue}`,
    params: nextParams
  };
}

async function runAppRequest(
  method: RequestMethod,
  url = '',
  params: Record<string, any> = {},
  options: AppRequestMethodOptions = {}
) {
  const {
    axiosOptions = {},
    ...customOptions
  } = options;

  const {
    paramsKey: customParamsKey,
    alertError = true,
    appendPathOnGet = false
  } = customOptions;

  let finalUrl = url;
  let finalParams = { ...params };
  if (appendPathOnGet && ['get', 'delete'].includes(method)) {
    const pathResult = buildPathUrl(finalUrl, finalParams);
    finalUrl = pathResult.url;
    finalParams = pathResult.params;
  }

  const paramsKey = customParamsKey ?? (/^(post|put|patch)$/i.test(method) ? 'data' : 'params');
  const { headers: customHeaders = {}, ...restAxiosOptions } = axiosOptions;

  try {
    const response = await requestClient.request(finalUrl, {
      ...restAxiosOptions,
      headers: buildRequestHeaders(customHeaders as Record<string, any>),
      method: method.toUpperCase() as Uppercase<RequestMethod>,
      [paramsKey]: finalParams,
      responseReturn: 'body'
    });

    return resolveRequestResult(response, customOptions);
  } catch (error: any) {
    handleRequestError(error, alertError);
    throw error;
  }
}

async function runUploadRequest(
  url = '',
  data: AppRequestUploadData,
  options: AppRequestMethodOptions = {}
) {
  const {
    axiosOptions = {},
    ...customOptions
  } = options;
  const { alertError = true } = customOptions;
  const { headers: customHeaders = {}, ...restAxiosOptions } = axiosOptions;

  try {
    const response = await requestClient.upload(url, data, {
      ...restAxiosOptions,
      headers: buildRequestHeaders(customHeaders as Record<string, any>, ''),
      responseReturn: 'body'
    });

    return resolveRequestResult(response, customOptions);
  } catch (error: any) {
    handleRequestError(error, alertError);
    throw error;
  }
}

async function runDownloadRequest(
  url = '',
  params: Record<string, any> = {},
  options: AppRequestDownloadOptions = {}
) {
  const {
    axiosOptions = {},
    responseReturn = 'body',
    ...customOptions
  } = options;
  const { alertSuccess = false, alertError = true } = customOptions;
  const { headers: customHeaders = {}, ...restAxiosOptions } = axiosOptions;

  try {
    const response = await requestClient.download(url, {
      ...restAxiosOptions,
      headers: buildRequestHeaders(customHeaders as Record<string, any>),
      params,
      responseReturn
    });

    if (alertSuccess) {
      successHandler('下载成功');
    }

    return responseReturn === 'raw' ? response : unwrapResponseBody(response);
  } catch (error: any) {
    handleRequestError(error, alertError);
    throw error;
  }
}

export const appRequest = {
  request(method: RequestMethod, url: string, params: Record<string, any> = {}, options: AppRequestMethodOptions = {}) {
    return runAppRequest(method, url, params, options);
  },
  get(url: string, params: Record<string, any> = {}, options: AppRequestMethodOptions = {}) {
    return runAppRequest('get', url, params, options);
  },
  post(url: string, params: Record<string, any> = {}, options: AppRequestMethodOptions = {}) {
    return runAppRequest('post', url, params, options);
  },
  put(url: string, params: Record<string, any> = {}, options: AppRequestMethodOptions = {}) {
    return runAppRequest('put', url, params, options);
  },
  delete(url: string, params: Record<string, any> = {}, options: AppRequestMethodOptions = {}) {
    return runAppRequest('delete', url, params, options);
  },
  patch(url: string, params: Record<string, any> = {}, options: AppRequestMethodOptions = {}) {
    return runAppRequest('patch', url, params, options);
  },
  upload(url: string, data: AppRequestUploadData, options: AppRequestMethodOptions = {}) {
    return runUploadRequest(url, data, options);
  },
  download(url: string, params: Record<string, any> = {}, options: AppRequestDownloadOptions = {}) {
    return runDownloadRequest(url, params, options);
  }
};
