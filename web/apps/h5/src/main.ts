import { createApp } from 'vue';
import App from '@/App.vue';
import router from '@/router';
import h5Ui from '@vue-scaffold/h5-ui';
import '@/styles/index.scss';

// rem 适配需要先于页面交互生效，避免首屏布局闪动。
import './utils/flexible';

const app = createApp(App);

app.use(router);
app.use(h5Ui);

app.mount('#app');
