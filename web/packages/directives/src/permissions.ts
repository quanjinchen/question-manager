import type { Directive } from 'vue';
import { messageAlert, hasStoredPermission } from '@vue-scaffold/utils';
import type { Router } from 'vue-router';

let routerInstance: Router | null = null;

export function setDirectiveRouterInstance(router: Router) {
  routerInstance = router;
}

export const permissions: Directive = {
  mounted(el, binding) {
    if (!hasStoredPermission(binding.value)) {
      el.remove();
    }
  }
};

export const permissionsJump: Directive = {
  mounted(el, binding) {
    const value = binding.value ?? {};
    const canJump = hasStoredPermission(value.permissions);

    el.addEventListener('click', () => {
      if (!canJump) {
        messageAlert({ type: 'warning', message: value.message ?? 'You do not have access to this page.' });
        return;
      }
      if (!routerInstance || !value.path) {
        return;
      }
      routerInstance.push({ path: value.path, query: value.query ?? {} });
    });
  }
};
