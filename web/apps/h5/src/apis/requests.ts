import { appRequest } from '@/apis/app-request';

export const $apis = {
  questionPortal: {
    login(params: Record<string, any>) {
      return appRequest.post('/consumer/question-portal/login', params, { alertError: false });
    },
    logout() {
      return appRequest.post('/consumer/question-portal/logout', {});
    },
    listMyCategory() {
      return appRequest.post('/consumer/question-portal/list-my-question-category', {});
    },
    listQuestions(params: Record<string, any>) {
      return appRequest.get('/consumer/question-portal/list-question-by-category-id', params, {
        appendPathOnGet: true,
      });
    },
    submitAnswer(params: Record<string, any>) {
      return appRequest.post('/consumer/question-portal/submit-question-answer', params);
    },
    recordDetail(params: Record<string, any>) {
      return appRequest.get('/consumer/question-portal/get-question-answer-record-by-id', params, {
        appendPathOnGet: true,
      });
    }
  }
};
