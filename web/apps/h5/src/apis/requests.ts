import { appRequest } from '@/apis/app-request';

export type AuthAccountParams = {
  certToken: string;
  fullName: string;
  idCard: string;
};

export type FaceAuthParams = AuthAccountParams & {
  faceImageBase64: string;
};

export const $apis = {
  auth: {
    checkCertToken(params: Pick<AuthAccountParams, 'certToken'>) {
      return appRequest.post('/auth/face/check-certToken', params, { alertError: false });
    },
    queryAccount(params: AuthAccountParams) {
      return appRequest.post('/auth/face/query-account', params, { alertError: false });
    },
    registerAccount(params: FaceAuthParams) {
      return appRequest.post('/auth/face/register-account', params);
    },
    compareFace(params: FaceAuthParams) {
      return appRequest.post('/auth/face/compare-face', params, { alertError: false });
    }
  }
};
