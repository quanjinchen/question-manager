# Vue Manager Scaffold - Claude 开发指南

本文档包含项目的开发规范和 AI 辅助开发的指导原则。

## 代码风格规范

### 1. 组件编写风格

所有 Vue 组件都应遵循 `dataInfo` 风格：

```typescript
const dataInfo: any = reactive({
  // 状态
  loading: false,
  list: [],
  
  // 计算属性
  get params() {
    return { /* ... */ };
  },
  
  // 方法
  async getData() {
    // ...
  },
  
  // 初始化
  async init() {
    // ...
  }
});

const { loading, list } = toRefs(dataInfo);

// 初始化
dataInfo.init();

// 暴露
defineExpose({ dataInfo });
```

**关键点：**
- 使用 `reactive` 创建 `dataInfo` 对象集中管理所有状态和方法
- 使用 getter 定义计算属性
- 所有方法都定义在 `dataInfo` 对象内
- 使用 `toRefs` 解构需要响应式的属性
- 使用 `defineExpose` 暴露 `dataInfo`

### 2. 导入路径规范

**始终使用 `@/` 路径别名，不使用相对路径。**

✅ 正确：
```typescript
import UserFormDialog from '@/views/user/components/UserFormDialog.vue';
import { requests } from '@/api/requests';
import type { UserRecord } from '@/types/domain';
```

❌ 错误：
```typescript
import UserFormDialog from './components/UserFormDialog.vue';
import { requests } from '../../api/requests';
```

**原因：** 路径别名使导入更一致、更易于重构，避免相对路径解析问题。

### 3. API 请求规范

所有 API 的 URL 都集中在 `src/api/requests.ts` 中管理，调用时只传递参数：

```typescript
// requests.ts
export const requests = {
  users: {
    list(params: Record<string, any> = {}) {
      return appRequest.post('/api/user/list-user', params);
    }
  }
};

// 使用
const result = await requests.users.list({ pageNum: 1, pageSize: 10 });
```

### 4. 表格配置分离

表格配置应提取到独立的 `tables/` 目录：

```
views/
  user/
    tables/
      User.ts          # 表格配置
    components/        # 页面组件
    User.vue          # 页面主文件
```

### 5. 组件组织规范

页面特定的弹窗组件应放在对应页面的 `components/` 目录下，不放在全局 `components/` 目录：

```
views/
  user/
    components/
      UserFormDialog.vue
      GrantUserRolesDialog.vue
    User.vue
```

### 6. 弹窗组件解耦

弹窗组件应内部处理数据加载和提交逻辑，与父页面解耦：

```typescript
// 弹窗内部
const dataInfo = reactive({
  async loadData() {
    // 加载数据
  },
  async handleSubmit() {
    // 提交逻辑
    await requests.users.save(/* ... */);
    messageAlert({ message: '保存成功' });
    emit('success');
  }
});

// 父页面只需要
<UserFormDialog
  v-model="dialogVisible"
  :record="selectedRecord"
  @success="getList()"
/>
```

### 7. 操作分发模式

`handleAction` 方法只做分发，具体操作逻辑提取到独立方法：

```typescript
const dataInfo = reactive({
  // 独立的操作方法
  openEdit(row) { /* ... */ },
  async deleteUser(row) { /* ... */ },
  
  // 分发器
  async handleAction(row, action) {
    const actionMap = {
      edit: () => this.openEdit(row),
      delete: () => this.deleteUser(row),
    };
    const handler = actionMap[action.key];
    if (handler) await handler();
  }
});
```

### 8. CSS 样式规范

**不使用 `display: grid` 布局。**

使用 flexbox 或其他布局方式替代：

```scss
// ✅ 使用 flexbox
.container {
  display: flex;
  flex-direction: column;
}

// ❌ 不使用 grid
.container {
  display: grid;
}
```

## 项目结构

```
src/
  api/
    requests.ts           # API 请求集中管理
  views/
    user/
      tables/
        User.ts          # 表格配置
      components/        # 页面组件
        UserFormDialog.vue
        GrantUserRolesDialog.vue
      User.vue          # 页面主文件
  types/
    domain.ts           # 类型定义
```

## 开发流程

1. **新增页面**：
   - 创建页面目录结构（`tables/`、`components/`）
   - 使用 `dataInfo` 风格编写主页面
   - 表格配置提取到 `tables/` 目录
   - 弹窗组件放在 `components/` 目录

2. **新增 API**：
   - 在 `requests.ts` 中添加 API 定义
   - URL 固定在 requests 中，调用时只传参数

3. **新增组件**：
   - 使用 `dataInfo` 风格
   - 内部处理数据加载和提交逻辑
   - 通过 `success` 事件通知父组件

## 注意事项

- 所有导入使用 `@/` 路径别名
- 组件使用 `dataInfo` 风格集中管理状态和方法
- 弹窗组件与页面解耦，内部处理业务逻辑
- 表格配置独立文件管理
- 不使用 `display: grid` 布局
