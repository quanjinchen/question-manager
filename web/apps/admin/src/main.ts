import { createApp } from 'vue';
import ElementPlus from 'element-plus';
import zhCn from 'element-plus/es/locale/lang/zh-cn';
import App from '@/App.vue';
import router from '@/router';
import pcUi from '@vue-scaffold/pc-ui';
import { createDirectivesPlugin, setDirectiveRouterInstance } from '@vue-scaffold/directives';
import { setRouterInstance } from '@/router/router-instance';
import { createWorkspacePinia } from '@/stores';
import '@vue-scaffold/styles';

const app = createApp(App);
const pinia = createWorkspacePinia();

app.use(pinia);

app.use(router);
app.use(ElementPlus, { locale: zhCn });
app.use(createDirectivesPlugin());
app.use(pcUi);

setRouterInstance(router);
setDirectiveRouterInstance(router);

app.mount('#app');
