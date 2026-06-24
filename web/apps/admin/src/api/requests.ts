import { appRequest, type AppRequestMethodOptions, type AppRequestUploadData } from '@/api/app-request';

type RequestParams = Record<string, any>;

export const $apis = {
  login: {
    accountLogin(params: RequestParams = {}) {
      return appRequest.post('/api/admin/login', params, { needLogin: false });
    },
    logout(params: RequestParams = {}) {
      return appRequest.post('/api/admin/logout', params);
    },
    getLoginInfo(params: RequestParams = {}) {
      return appRequest.post('/api/admin/get-login-info', params);
    },
    getCaptcha(params: RequestParams = {}, options: AppRequestMethodOptions = {}) {
      return appRequest.post('/api/admin/get-captcha', params, options);
    }
  },
  dashboard: {
    summary(params: RequestParams = {}) {
      return appRequest.post('/api/dashboard/summary', params);
    }
  },
  operationLogs: {
    page(params: RequestParams = {}) {
      return appRequest.post('/api/operation-log/list-operation-log', params);
    }
  },
  users: {
    list(params: RequestParams = {}) {
      return appRequest.post('/api/user/list-user', params);
    },
    detail(params: RequestParams = {}) {
      return appRequest.get('/api/user/get-user-by-id', params, {
        appendPathOnGet: true,
      });
    },
    create(params: RequestParams = {}) {
      return appRequest.post('/api/user/create-user', params);
    },
    update(params: RequestParams = {}) {
      return appRequest.post('/api/user/update-user', params);
    },
    delete(params: RequestParams = {}) {
      return appRequest.post('/api/user/delete-user', params);
    },
    resetPassword(params: RequestParams = {}) {
      return appRequest.post('/api/user/reset-user-password', params);
    }
  },
  files: {
    upload(data: AppRequestUploadData) {
      return appRequest.upload('/api/file/upload-file', data);
    },
    downloadUrl(fileId: string | number) {
      return `/api/file/download-file/${fileId}`;
    },
    download(fileId: string | number, options: AppRequestMethodOptions = {}) {
      return appRequest.download(`/api/file/download-file/${fileId}`, {}, {
        responseReturn: 'raw',
        ...options,
      });
    }
  },
  organizations: {
    tree(params: RequestParams = {}) {
      return appRequest.post('/api/org/list-all-org-tree', params);
    },
    detail(params: RequestParams = {}) {
      return appRequest.get('/api/org/detail', params, {
        appendPathOnGet: true,
      });
    },
    save(params: RequestParams = {}) {
      return appRequest.post('/api/org/create-org', params);
    },
    update(params: RequestParams = {}) {
      return appRequest.post('/api/org/update-org', params);
    },
    delete(params: RequestParams = {}) {
      return appRequest.post('/api/org/delete-org', params);
    }
  },
  menus: {
    tree(params: RequestParams = {}) {
      return appRequest.post('/api/menu/list-all-menu-tree', params);
    },
    detail(params: RequestParams = {}) {
      return appRequest.get('/api/menu/get-menu-by-id', params, {
        appendPathOnGet: true,
      });
    },
    save(params: RequestParams = {}) {
      return appRequest.post('/api/menu/create-menu', params);
    },
    update(params: RequestParams = {}) {
      return appRequest.post('/api/menu/update-menu', params);
    },
    delete(params: RequestParams = {}) {
      return appRequest.post('/api/menu/delete-menu', params);
    }
  },
  roles: {
    list(params: RequestParams = {}) {
      return appRequest.post('/api/role/list-role', params);
    },
    detail(params: RequestParams = {}) {
      return appRequest.get('/api/role/detail', params, {
        appendPathOnGet: true,
      });
    },
    save(params: RequestParams = {}) {
      return appRequest.post('/api/role/create-role', params);
    },
    update(params: RequestParams = {}) {
      return appRequest.post('/api/role/update-role', params);
    },
    delete(params: RequestParams = {}) {
      return appRequest.post('/api/role/delete-role', params);
    },
    grantInfo(params: RequestParams = {}) {
      return appRequest.get('/api/role/get-role-grant-info-by-role-id', params, {
        appendPathOnGet: true,
      });
    },
    grantMenus(params: RequestParams = {}) {
      return appRequest.post('/api/role/grant-role-menus', params);
    },
    assignableUsers(params: RequestParams = {}) {
      return appRequest.post('/api/role/list-role-assignable-users', params);
    },
    roleUsers(params: RequestParams = {}) {
      return appRequest.post('/api/role/list-role-users', params);
    },
    grantUsers(params: RequestParams = {}) {
      return appRequest.post('/api/role/grant-role-users', params);
    }
  },
  userRoles: {
    list(params: RequestParams = {}) {
      return appRequest.post('/api/user-role/list-user-role', params);
    },
    grant(params: RequestParams = {}) {
      return appRequest.post('/api/user-role/grant-user-roles', params);
    }
  },
  orgUsers: {
    list(params: RequestParams = {}) {
      return appRequest.post('/api/org-user/list-org-user', params);
    },
    grant(params: RequestParams = {}) {
      return appRequest.post('/api/org-user/grant-org-users', params);
    }
  },
  questionCategories: {
    list(params: RequestParams = {}) {
      return appRequest.post('/api/question-category/list-question-category', params);
    },
    listAll(params: RequestParams = {}) {
      return appRequest.post('/api/question-category/list-all-question-category', params);
    },
    detail(params: RequestParams = {}) {
      return appRequest.get('/api/question-category/get-question-category-by-id', params, {
        appendPathOnGet: true,
      });
    },
    create(params: RequestParams = {}) {
      return appRequest.post('/api/question-category/create-question-category', params);
    },
    update(params: RequestParams = {}) {
      return appRequest.post('/api/question-category/update-question-category', params);
    },
    delete(params: RequestParams = {}) {
      return appRequest.post('/api/question-category/delete-question-category', params);
    },
    grantInfo(params: RequestParams = {}) {
      return appRequest.get('/api/question-category/get-question-category-grant-by-user-id', params, {
        appendPathOnGet: true,
      });
    },
    grant(params: RequestParams = {}) {
      return appRequest.post('/api/question-category/grant-question-category', params);
    }
  },
  questions: {
    list(params: RequestParams = {}) {
      return appRequest.post('/api/question/list-question', params);
    },
    detail(params: RequestParams = {}) {
      return appRequest.get('/api/question/get-question-by-id', params, {
        appendPathOnGet: true,
      });
    },
    create(params: RequestParams = {}) {
      return appRequest.post('/api/question/create-question', params);
    },
    update(params: RequestParams = {}) {
      return appRequest.post('/api/question/update-question', params);
    },
    delete(params: RequestParams = {}) {
      return appRequest.post('/api/question/delete-question', params);
    }
  },
  questionAnswerRecords: {
    list(params: RequestParams = {}) {
      return appRequest.post('/api/question-answer-record/list-question-answer-record', params);
    },
    detail(params: RequestParams = {}) {
      return appRequest.get('/api/question-answer-record/get-question-answer-record-by-id', params, {
        appendPathOnGet: true,
      });
    }
  }
};
