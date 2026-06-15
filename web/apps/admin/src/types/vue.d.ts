import type $utils from '@vue-scaffold/utils';

declare global {
  type AppUtils = typeof $utils;
}

declare module 'vue' {
  interface ComponentCustomProperties {
    $utils: typeof $utils;
  }
}

export {};
