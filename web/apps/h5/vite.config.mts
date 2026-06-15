import { defineConfig, loadEnv } from 'vite';
import vue from '@vitejs/plugin-vue';
import { resolve } from 'node:path';

function manualChunks(id: string) {
  if (!id.includes('node_modules')) {
    return;
  }

  if (id.includes('node_modules/vant')) {
    return 'vant';
  }

  if (id.includes('node_modules/vue-router')) {
    return 'app-core';
  }

  if (id.includes('node_modules/vue')) {
    return 'vue-vendor';
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
        '@vue-scaffold/h5-ui': resolve(__dirname, '../../packages/h5-ui/src/index.ts'),
        '@vue-scaffold/utils': resolve(__dirname, '../../packages/utils/src/index.ts')
      }
    },
    server: {
      host: '0.0.0.0',
      port: Number(env.VITE_SERVER_PORT || 5174),
      strictPort: true,
      proxy: {
        '/auth': {
          target: env.VITE_PROXY_TARGET || 'http://127.0.0.1:18081',
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
