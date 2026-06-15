# AGENTS.md

工作约定

读取文件使用 UTF8 编码。
写入文件时注意编码正确，避免写入乱码文件。

前端开发约定

1. `apps/admin` 内部引用默认使用 `@/` 指向 `src`，尽量不要使用 `../../` 这类相对路径。
2. 跨 workspace 包引用时，统一使用 `@vue-scaffold/api`、`@vue-scaffold/utils` 这类别名，不要直接写包目录相对路径。
3. 如果是同一目录下的紧邻文件，例如 `./types`、`./constants` 这种局部小范围引用，可以继续使用相对路径。
4. 只有在当前工程没有提供别名、且改动别名成本明显高于收益时，才允许使用相对路径，并且应尽量控制层级。
5. 新增公共能力时，优先沉淀到 `packages/*` 下的公共包，不要重复散落在 `apps/admin` 业务目录中。
6. 公共工具方法优先放在 `packages/utils`，请求封装优先放在 `packages/api`，类型定义优先放在 `packages/types`。
7. 修改前端代码时，保持现有分层风格，优先通过公共封装接入，不要在业务页面里重复拼请求头、鉴权逻辑或通用格式转换。

分层职责约定

1. `views` 负责页面展示、交互组织和页面级状态编排，不直接承载通用请求逻辑。
2. 默认不新增 `services` 层；简单 CRUD 和单页面请求直接在页面中调用 `$apis` 即可。
3. 只有明显复杂的业务编排、多接口串联、登录流程归一化这类场景，才允许保留或新增 `services`。
4. `packages/api` 负责通用请求基础设施、请求实例、统一请求头、错误处理和通用请求封装。
5. `stores` 负责跨页面共享状态、登录态、菜单态、持久化状态，不要把一次性页面状态滥用到 store 中。
6. `packages/utils` 负责纯工具函数、无业务页面耦合的公共方法。
7. `packages/types` 负责公共类型定义；如果类型会被多个模块复用，优先抽到这里。
8. `packages/pc-ui` 负责 PC 端 Element Plus 可复用组件封装，`packages/h5-ui` 负责 H5 端 Vant 可复用组件封装，业务页面专属内容不要反向塞进公共 UI 包。

编码习惯约定

1. 页面里不要直接调用裸 `axios`，统一走 `packages/api` 或现有请求封装。
2. 页面里不要重复写请求时间戳、请求 ID、鉴权头等通用逻辑，这类能力统一放在请求层。
3. 多个页面复用的结果转换、字段归一化、菜单映射、表单默认值处理，优先提炼为页面内局部函数或 `utils`，不要为了轻量场景机械新增 `services`。
4. 如果逻辑带明显业务语义，例如“登录结果归一化”“多接口串联后的统一返回”，才考虑放进 `services`。
5. `utils` 保持无状态、轻依赖、可复用，不放页面上下文强相关逻辑。
6. 新增方法、复杂分支和兼容逻辑时，补充必要的中文注释，说明业务意图和特殊原因。
7. 表单输入组件默认补充 placeholder，不要留空。
8. 输入类组件的 placeholder 统一写成“请输入 + 表单名称”，例如“请输入用户名”“请输入角色编码”。
9. 选择类组件的 placeholder 统一写成“请选择 + 表单名称”，例如“请选择所属组织”“请选择菜单类型”。
10. 如果字段名称较长，placeholder 也应保留完整业务语义，不要随意缩写成模糊表述。
11. 查询分页、查询详情、列表刷新这类请求，发起前必须显式开启 loading，并在 `finally` 中关闭 loading。
12. 页面列表型请求默认维护页面级 loading，例如表格 `:loading=\"loading\"`，不要只请求不显示加载态。
13. 点击按钮触发的异步事件，如果会发请求，必须增加对应的按钮 loading 或操作 loading，并绑定到按钮上。
14. 所有引入 loading 的异步请求，统一包裹 `try/finally`，只在 `finally` 中关闭 loading，避免中途 `return` 导致 loading 残留。
15. 新增、编辑、删除、重置、刷新成功后，如果外层有列表、树或汇总数据，必须同步刷新外层数据，不要只关弹窗不刷新页面。
16. 表单弹窗的提交 loading 必须覆盖到父级真实请求完成，子组件触发 `emit('submit')` 时需要 `await` 父组件返回的 Promise。

目录落点约定

1. 通用请求头、请求拦截、响应处理、错误提示统一放在 `packages/api`。
2. 权限判断、存储封装、时间处理、字符串处理、ID 生成等通用方法统一放在 `packages/utils`。
3. 页面专属的弹窗配置、表格列定义、筛选项、局部类型，可以放在页面目录下。
4. 所有表格的 `tableInfo` 必须拆到独立文件，统一放在当前模块目录下的 `tables/` 中，不允许在页面 `.vue` 文件里内联定义 `const tableInfo = ...`。
5. 页面使用表格时，统一通过 `import tableInfo from '@/views/<module>/tables/<Name>'` 引入，保持页面只负责状态和交互，不承载列配置细节。
6. 如果一个能力已经在 `packages/*` 中存在，就不要在 `apps/admin/src` 里重复造一份。
7. 新增公共能力前，先检查现有 `packages/*` 是否已有相近实现，优先复用或扩展。

CRUD 模块开发规范

1. 新增后台 CRUD 模块时，默认参考 `User` 模块结构实现，不要每个模块各写一套风格。
2. 每个业务模块固定放在 `apps/admin/src/views/<module>/`，列表页入口文件命名为 `<Module>.vue`。
3. 模块内弹窗组件统一放在 `components/`，表格列配置统一放在 `tables/`。
4. 即使模块只有一个表格，也必须建立 `tables/` 目录并把表格配置独立成文件。
5. 模块对应的接口统一收口到 `apps/admin/src/api/requests.ts` 的 `$apis.<module>` 下，不允许页面里直接写接口 URL。
6. 页面列表、表单弹窗、表格列配置、业务类型保持清晰分层，页面不要同时承载列表逻辑和表单细节。
7. 业务实体类型统一定义在 `apps/admin/src/types/domain.ts`，不要长期在页面里使用 `any`。

推荐目录结构

```text
apps/admin/src/views/user/
  User.vue
  components/
    UserFormDialog.vue
  tables/
    User.ts
```

接口约定

1. 全局请求集合统一命名为 `$apis`。
2. 接口按业务域分组，例如 `$apis.users`、`$apis.roles`、`$apis.organizations`。
3. 标准 CRUD 命名统一为 `list`、`detail`、`create`、`update`、`delete`。
4. 非 CRUD 动作使用明确业务语义命名，例如 `resetPassword`、`grantMenus`、`grant`。
5. 页面只能通过 `$apis.xxx.xxx()` 发请求，不允许直接调用裸 `axios` 或直接拼接接口路径。

推荐示例

```ts
export const $apis = {
  users: {
    list(params: Record<string, any> = {}) {
      return appRequest.post('/api/user/list-user', params);
    },
    detail(params: Record<string, any> = {}) {
      return appRequest.get('/api/user/get-user-by-id', params, {
        appendPathOnGet: true,
      });
    },
    create(params: Record<string, any> = {}) {
      return appRequest.post('/api/user/create-user', params);
    },
    update(params: Record<string, any> = {}) {
      return appRequest.post('/api/user/update-user', params);
    },
    delete(params: Record<string, any> = {}) {
      return appRequest.post('/api/user/delete-user', params);
    },
  }
};
```

列表页规范

1. 列表页入口统一为 `views/<module>/<Module>.vue`。
2. 列表页负责搜索、分页、列表请求、行操作分发、弹窗开关，不负责表单内部实现。
3. 页面状态统一收口到一个 `dataInfo` 中，至少包含：
   `pageInfo`、`searchParams`、`list`、`total`、`loading`、`actionLoading`、`dialogVisible`、`selectedRecord`。
4. 列表请求参数统一通过 `get params()` 组合，避免在多个方法中重复拼接。
5. 搜索默认走防抖，建议 `300ms`。
6. 删除、重置、授权等有副作用操作，必须先确认，再请求，再刷新列表。
7. 表格行操作统一通过 `handleAction` 分发，不要把大量业务逻辑直接塞进表格列定义。
8. 页面中不要直接声明 `const tableInfo = ...`，表格列配置必须从 `tables/*.ts` 导入。
9. `handleAction` 内部不要使用 `this`，统一直接调用 `dataInfo.xxx()` 或读取 `dataInfo.xxx`，避免上下文不清晰。
10. `tableInfo.columns` 中，有 `prop` 的列不要重复声明 `key`，默认直接使用 `prop` 作为唯一值；只有 `$ordinal`、`$action` 这类没有 `prop` 的特殊列才保留 `key`。

列表页示例

```vue
<script setup lang="ts" name="User">
import { reactive, toRefs } from "vue";
import { debounce, messageAlert, messageConfirm } from "@vue-scaffold/utils";
import { $apis } from "@/api/requests";
import type { UserRecord } from "@/types/domain";
import UserFormDialog from "@/views/user/components/UserFormDialog.vue";
import tableInfo from "@/views/user/tables/User";

const dataInfo = reactive({
  pageInfo: { pageNum: 1, pageSize: 10 },
  searchParams: {
    username: "",
    fullName: "",
    phone: "",
    email: "",
  },
  dialogVisible: false,
  selectedRecord: null as UserRecord | null,
  total: 0,
  list: [] as UserRecord[],
  loading: false,
  actionLoading: false,
  get params() {
    return {
      ...this.searchParams,
      ...this.pageInfo,
    };
  },
  async getList() {
    this.loading = true;
    try {
      const data = await $apis.users.list(this.params);
      this.total = data?.total ?? 0;
      this.list = data?.records ?? [];
    } finally {
      this.loading = false;
    }
  },
  search() {
    this.pageInfo.pageNum = 1;
    this.getList();
  },
  debounceSearch: debounce(function (this: any) {
    this.search();
  }, 300),
  openCreate() {
    this.selectedRecord = null;
    this.dialogVisible = true;
  },
  openEdit(row: UserRecord) {
    this.selectedRecord = row;
    this.dialogVisible = true;
  },
  async deleteUser(row: UserRecord) {
    if (this.actionLoading) return;
    this.actionLoading = true;
    try {
      await messageConfirm(`确认删除用户"${row.fullName || row.userName}"吗？`);
      await $apis.users.delete({ userId: Number(row.id) });
      messageAlert({ message: "用户删除成功" });
      await this.getList();
    } finally {
      this.actionLoading = false;
    }
  },
  async handleAction(row: UserRecord, action: Record<string, any>) {
    const actionMap: Record<string, () => void | Promise<void>> = {
      edit: () => dataInfo.openEdit(row),
      delete: () => dataInfo.deleteUser(row),
    };
    await actionMap[action.key]?.();
  },
});

const { pageInfo, searchParams, list, total, loading } = toRefs(dataInfo);
</script>
```

表单弹窗规范

1. 新增和编辑统一复用一个 `<Module>FormDialog.vue`，不要拆成两个组件。
2. 组件职责只包含弹窗显示、表单状态、校验、详情回填、提交请求。
3. 弹窗只通过 `emit("success")` 通知父页面刷新，不在弹窗里直接处理外层列表。
4. `props` 统一使用：
   `modelValue: boolean`
   `selectItem?: XxxRecord | null`
5. `emits` 统一使用：
   `"update:modelValue"`
   `success`
6. 组件内部统一维护 `formRef`、`visible`、`formData`、`rules`、`loading`、`submitLoading`。
7. `loading` 表示页面级 loading，用于详情加载和提交时遮罩表单区域。
8. `submitLoading` 仅用于保存按钮 loading。
9. 弹窗关闭时必须重置表单值、校验状态和临时状态。
10. 打开弹窗时，如果是编辑态则请求详情；如果是新增态则直接使用初始表单值。
11. 提交前必须先执行表单校验，并阻止重复提交。
12. 提交成功后统一提示成功、关闭弹窗并触发 `success` 事件。

表单弹窗示例

```vue
<template>
  <AppDialog
    v-model="visible"
    :modal-props="modalProps"
    :footer-props="footerProps"
  >
    <el-form
      ref="formRef"
      v-loading="loading"
      :model="formData"
      :rules="dataInfo.rules"
      label-position="top"
    >
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="用户名" prop="username">
            <AppInput v-model="formData.username" v-trim placeholder="请输入用户名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="姓名" prop="fullName">
            <AppInput v-model="formData.fullName" v-trim placeholder="请输入姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="备注" prop="remark">
            <AppInput
              v-model="formData.remark"
              placeholder="请输入备注"
              :input-props="{ type: 'textarea', rows: 3 }"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </AppDialog>
</template>

<script setup lang="ts" name="UserFormDialog">
import { computed, reactive, ref, toRefs, watch } from "vue";
import type { FormInstance } from "element-plus";
import { messageAlert, useVModel } from "@vue-scaffold/utils";
import { $apis } from "@/api/requests";
import type { UserRecord } from "@/types/domain";

const props = defineProps<{
  modelValue: boolean;
  selectItem?: UserRecord | null;
}>();

const emit = defineEmits<{
  "update:modelValue": [boolean];
  success: [];
}>();

const formRef = ref<FormInstance>();
const visible = useVModel(props, emit as any);

const dataInfo = reactive({
  formData: {
    username: "",
    fullName: "",
    phone: "",
    email: "",
    status: "",
    remark: "",
  },
  rules: {
    username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
    fullName: [{ required: true, message: "请输入姓名", trigger: "blur" }],
  },
  loading: false,
  submitLoading: false,
  get isEdit() {
    return Boolean(props.selectItem?.id);
  },
  initForm() {
    formRef.value?.resetFields();
    formRef.value?.clearValidate();
  },
  async getDetail() {
    if (!props.selectItem?.id) return;

    this.loading = true;
    try {
      const detail = await $apis.users.detail({ id: Number(props.selectItem.id) });
      this.formData = {
        ...detail,
      };
    } finally {
      this.loading = false;
    }
  },
  get params() {
    return { ...this.formData };
  },
  async handleSubmit() {
    await formRef.value?.validate();
    if (this.submitLoading) return;

    this.submitLoading = true;
    this.loading = true;
    try {
      await $apis.users[this.isEdit ? "update" : "create"]({
        ...this.params,
      });
      messageAlert({ message: "操作成功" });
      visible.value = false;
      emit("success");
    } finally {
      this.submitLoading = false;
      this.loading = false;
    }
  },
});

const modalProps = computed(() => ({
  title: `${dataInfo.isEdit ? "编辑" : "新增"}用户`,
  width: 720,
}));

const footerProps = computed(() => ({
  buttons: [
    { text: "取消", close: true, buttonProps: {} },
    {
      text: dataInfo.submitLoading ? "保存中..." : "保存",
      close: false,
      buttonProps: { type: "primary", loading: dataInfo.submitLoading },
      click: () => dataInfo.handleSubmit(),
    },
  ],
}));

watch(visible, (value) => {
  if (!value) {
    dataInfo.initForm();
    dataInfo.loading = false;
    dataInfo.submitLoading = false;
    return;
  }
  dataInfo.getDetail();
});

const { formData, loading } = toRefs(dataInfo);
</script>
```

命名约定

1. 列表页组件命名使用业务名，例如 `User`、`Role`、`Organization`。
2. 表单弹窗组件统一命名为 `<Module>FormDialog`。
3. 选中行数据统一命名为 `selectedRecord`。
4. 弹窗开关统一命名为 `dialogVisible`。
5. 页面级列表 loading 统一命名为 `loading`。
6. 行操作 loading 统一命名为 `actionLoading`。
7. 弹窗提交 loading 统一命名为 `submitLoading`。
8. 获取列表方法统一命名为 `getList`。
9. 弹窗详情方法统一命名为 `getDetail`。
10. 提交方法统一命名为 `handleSubmit`。

补充约束

1. 不要在页面和弹窗中混用 `requests`、`api`、`request` 等多个接口入口名称，统一使用 `$apis`。
2. 不要只给按钮加 loading，不给页面或表单区域加 loading。
3. 不要在模板中直接写复杂业务判断，尽量收口到 `computed` 或方法中。
4. 不要把父组件传入的 `selectItem` 直接当作表单对象修改。
5. 如果前后端字段不一致，统一在表单详情回填或提交前做一次集中映射，不要在页面各处散写兼容逻辑。
