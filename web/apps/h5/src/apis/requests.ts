import { appRequest } from '@/apis/app-request';

export type AuthAccountParams = {
  certToken: string;
  fullName: string;
  idCard: string;
};

export const $apis = {
  auth: {
    checkCertToken(params: Pick<AuthAccountParams, 'certToken'>) {
      return appRequest.post('/auth/auth/check-certToken', params, { alertError: false });
    },
    queryAccount(params: AuthAccountParams) {
      return appRequest.post('/auth/auth/query-account', params, { alertError: false });
    },
    registerAccount(params: AuthAccountParams) {
      return appRequest.post('/auth/auth/register-account', params);
    }
  }
};
