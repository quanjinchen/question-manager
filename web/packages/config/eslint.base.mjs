import pluginVue from 'eslint-plugin-vue';
import { defineConfigWithVueTs, vueTsConfigs, configureVueProject } from '@vue/eslint-config-typescript';

configureVueProject({
  tsSyntaxInTemplates: true,
  scriptLangs: ['ts'],
  rootDir: import.meta.dirname
});

export default defineConfigWithVueTs(
  pluginVue.configs['flat/essential'],
  pluginVue.configs['flat/recommended'],
  vueTsConfigs.base,
  {
    files: ['**/*.{ts,vue}'],
    languageOptions: {
      globals: {
        defineProps: 'readonly',
        defineEmits: 'readonly',
        defineExpose: 'readonly',
        withDefaults: 'readonly'
      }
    },
    rules: {
      'vue/multi-word-component-names': 'off',
      '@typescript-eslint/no-explicit-any': 'off',
      'vue/require-default-prop': 'off',
      'vue/require-explicit-emits': 'off'
    }
  }
);
