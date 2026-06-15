import type { App } from 'vue';
import { trim } from './trim';
import { permissions, permissionsJump } from './permissions';

export function installDirectives(app: App) {
  app.directive('trim', trim);
  app.directive('permissions', permissions);
  app.directive('permissions-jump', permissionsJump);
}

export * from './plugin';
export * from './trim';
export * from './permissions';
