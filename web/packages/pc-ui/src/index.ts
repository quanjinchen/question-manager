const components = import.meta.glob('./components/App*.vue', { eager: true });

export * from './components';
export * from './composables';

export default {
  install(app: { component: (name: string, component: unknown) => void }) {
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
