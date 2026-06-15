import type { Directive } from 'vue';

export const trim: Directive = {
  mounted(el) {
    const input = el.querySelector('input');
    if (!input) {
      return;
    }
    input.addEventListener('blur', () => {
      input.value = input.value.trim();
      input.dispatchEvent(new Event('input', { bubbles: true }));
    });
  }
};

