import { defineConfig, loadEnv } from 'vite';
import vue from '@vitejs/plugin-vue';
import { resolve } from 'node:path';

function manualChunks(id: string) {
  if (!id.includes('node_modules')) {
    return;
  }

  if (id.includes('node_modules/echarts')) {
    return 'echarts';
  }

  if (id.includes('node_modules/@element-plus/icons-vue')) {
    return 'element-plus-icons';
  }

  if (id.includes('node_modules/element-plus')) {
    return 'element-plus';
  }

  if (
    id.includes('node_modules/vue-router') ||
    id.includes('node_modules/pinia') ||
    id.includes('node_modules/pinia-plugin-persistedstate') ||
    id.includes('node_modules/nprogress')
  ) {
    return 'app-core';
  }

  if (id.includes('node_modules/vue')) {
    return 'vue-vendor';
  }

  if (id.includes('node_modules/axios')) {
    return 'axios';
  }

  if (id.includes('node_modules/dayjs')) {
    return 'dayjs';
  }

  if (id.includes('node_modules/crypto-js')) {
    return 'crypto-js';
  }

  if (id.includes('node_modules/bcryptjs')) {
    return 'bcryptjs';
  }
}

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');

  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': resolve(__dirname, 'src'),
        '@vue-scaffold/api': resolve(__dirname, '../../packages/api/src/index.ts'),
        '@vue-scaffold/constants': resolve(__dirname, '../../packages/constants/src/index.ts'),
        '@vue-scaffold/directives': resolve(__dirname, '../../packages/directives/src/index.ts'),
        '@vue-scaffold/styles': resolve(__dirname, '../../packages/styles/src/index.scss'),
        '@vue-scaffold/types': resolve(__dirname, '../../packages/types/src/index.ts'),
        '@vue-scaffold/pc-ui': resolve(__dirname, '../../packages/pc-ui/src/index.ts'),
        '@vue-scaffold/utils': resolve(__dirname, '../../packages/utils/src/index.ts')
      }
    },
    server: {
      host: '0.0.0.0',
      port: Number(env.VITE_SERVER_PORT || 5173),
      strictPort: true,
      proxy: {
        '/api': {
          target: env.VITE_PROXY_TARGET || 'http://127.0.0.1:18080',
          changeOrigin: true
        }
      }
    },
    css: {
      preprocessorOptions: {
        scss: {
          api: 'modern-compiler'
        }
      }
    },
    build: {
      rollupOptions: {
        output: {
          manualChunks
        }
      }
    }
  };
});
