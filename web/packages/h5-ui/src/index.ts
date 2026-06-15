import 'vant/lib/index.css';
import {
  Button,
  Cell,
  CellGroup,
  Checkbox,
  Field,
  Form,
  Grid,
  GridItem,
  Icon,
  Lazyload,
  Loading,
  NumberKeyboard,
  Picker,
  Popup
} from 'vant';

const components = import.meta.glob('./components/AppH5*.vue', { eager: true });
const vantPlugins = [
  Button,
  Cell,
  CellGroup,
  Checkbox,
  Field,
  Form,
  Grid,
  GridItem,
  Icon,
  Lazyload,
  Loading,
  NumberKeyboard,
  Picker,
  Popup
];

export * from './components';

export default {
  install(app: { component: (name: string, component: unknown) => void; use: (plugin: unknown) => void }) {
    vantPlugins.forEach(plugin => app.use(plugin));
    Object.entries(components).forEach(([path, value]: [string, any]) => {
      const component = value.default;
      const fileName = path.split('/').pop()?.replace('.vue', '') || '';
      const componentName = component?.name || component?.__name || fileName;
      if (componentName) {
        app.component(componentName, component);
      }
    });
  }
};
