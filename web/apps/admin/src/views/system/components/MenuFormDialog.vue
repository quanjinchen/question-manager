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
      <el-form-item label="上级菜单" prop="parentId">
        <el-tree-select
          v-model="formData.parentId"
          style="width: 100%"
          :data="menuTree"
          node-key="id"
          :props="{ label: 'menuName', children: 'children' }"
          placeholder="请选择上级菜单"
          clearable
          check-strictly
          default-expand-all
        />
      </el-form-item>

      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="菜单类型" prop="menuType">
            <AppSelect
              v-model="formData.menuType"
              :list="dictStore.menuTypeList"
              :select-props="{ placeholder: '请选择菜单类型' }"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="菜单名称" prop="menuName">
            <AppInput v-model="formData.menuName" v-trim placeholder="请输入菜单名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="排序" prop="orderNum">
            <AppInputNumber
              v-model="formData.orderNum"
              :input-number-props="{
                min: 1,
                max: 9999,
                style: 'width: 100%',
                placeholder: '请输入排序',
              }"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="路径" prop="path">
            <AppInput v-model="formData.path" v-trim placeholder="请输入路径" />
          </el-form-item>
        </el-col>
          <el-col :span="12">
          <el-form-item label="图标" prop="icon">
            <IconSelector v-model="formData.icon" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="权限编码" prop="menuCode">
            <AppInput v-model="formData.menuCode" v-trim placeholder="请输入权限编码" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="是否启用" prop="enabled">
            <AppSwitch v-model="formData.enabled" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </AppDialog>
</template>

<script setup lang="ts" name="MenuFormDialog">
import { computed, reactive, ref, toRefs, watch } from 'vue';
import $utils from '@vue-scaffold/utils';
import { useVModel } from '@vue-scaffold/hooks';
import type { FormInstance } from 'element-plus';
import { dictStore } from '@vue-scaffold/constants';
import { $apis } from '@/api/requests';
import type { MenuRecord } from '@/types/domain';
import { normalizeMenuType } from '@/router/menu';
import IconSelector from './IconSelector.vue';

type MenuFormData = Omit<MenuRecord, 'id' | 'children'>;

const ROOT_MENU_ID = '0';

const props = defineProps<{
  modelValue: boolean;
  selectItem?: MenuRecord | null;
  menus: MenuRecord[];
}>();

const emit = defineEmits<{
  'update:modelValue': [boolean];
  success: [];
}>();

const formRef = ref<FormInstance>();
const visible = useVModel(props, emit as any);

const modalProps = computed(() => ({
  title: `${dataInfo.isEdit ? '编辑' : '新增'}菜单`,
  width: 680,
}));

const footerProps = computed(() => ({
  buttons: [
    { text: '取消', close: true, buttonProps: {} },
    {
      text: submitLoading.value ? '保存中...' : '保存',
      close: false,
      buttonProps: { type: 'primary', loading: submitLoading.value },
      click: () => dataInfo.handleSubmit(),
    },
  ],
}));

const menuTree = computed(() => [
  {
    id: ROOT_MENU_ID,
    parentId: null,
    menuType: 'DIR' as const,
    menuName: '根级菜单',
    path: '',
    icon: '',
    menuCode: '',
    orderNum: 0,
    enabled: true,
    children: filterAvailableMenus(props.menus ?? [], props.selectItem?.id),
  },
]);

const dataInfo = reactive({
  formData: {
    parentId: ROOT_MENU_ID,
    menuType: 'MENU',
    menuName: '',
    path: '',
    icon: '',
    menuCode: '',
    orderNum: 1,
    enabled: true,
  } as MenuFormData,
  rules: {
    menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
    menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
    path: [{ required: true, message: '请输入路径', trigger: 'blur' }],
    menuCode: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  },
  loading: false,
  submitLoading: false,
  get isEdit() {
    return Boolean(props.selectItem?.id);
  },
  initForm() {
    this.formData = {
      parentId: ROOT_MENU_ID,
      menuType: 'MENU',
      menuName: '',
      path: '',
      icon: '',
      menuCode: '',
      orderNum: 1,
      enabled: true,
    };
    formRef.value?.clearValidate();
  },
  async getDetail() {
    if (!props.selectItem?.id) {
      if (props.selectItem?.parentId !== undefined && props.selectItem?.parentId !== null) {
        this.formData.parentId = String(props.selectItem.parentId);
      }
      return;
    }

    this.loading = true;
    try {
      const detail = await $apis.menus.detail({
        id: Number(props.selectItem.id),
      });
      this.formData = mapMenuDetail(detail);
    } finally {
      this.loading = false;
    }
  },
  get params() {
    return {
      id: this.isEdit ? Number(props.selectItem?.id) : undefined,
      parentId:
        this.formData.parentId === null ||
        this.formData.parentId === undefined ||
        this.formData.parentId === '' ||
        String(this.formData.parentId) === ROOT_MENU_ID
          ? 0
          : Number(this.formData.parentId),
      menuType: this.formData.menuType,
      menuName: this.formData.menuName,
      path: this.formData.path,
      icon: this.formData.icon,
      menuCode: this.formData.menuCode,
      orderNum: this.formData.orderNum,
      visible: this.formData.enabled,
    };
  },
  async handleSubmit() {
    await formRef.value?.validate();
    if (this.submitLoading) {
      return;
    }

    this.submitLoading = true;
    this.loading = true;
    try {
      await $apis.menus[this.isEdit ? 'update' : 'save'](this.params);
      $utils.Message.messageAlert({ message: this.isEdit ? '菜单更新成功' : '菜单创建成功' });
      visible.value = false;
      emit('success');
    } finally {
      this.submitLoading = false;
      this.loading = false;
    }
  },
});

watch(visible, value => {
  if (!value) {
    dataInfo.initForm();
    dataInfo.loading = false;
    dataInfo.submitLoading = false;
    return;
  }
  dataInfo.getDetail();
});

function mapMenuDetail(detail: Record<string, any>): MenuFormData {
  return {
    parentId:
      detail.parentId === null || detail.parentId === undefined || Number(detail.parentId) === 0
        ? ROOT_MENU_ID
        : String(detail.parentId),
    menuType: normalizeMenuType(detail.menuType),
    menuName: detail.menuName ?? detail.name ?? '',
    path: detail.path ?? '',
    icon: detail.icon ?? '',
    menuCode: detail.menuCode ?? detail.permissionCode ?? '',
    orderNum: Number(detail.orderNum ?? detail.sortOrder ?? 1),
    enabled: detail.visible !== false && Number(detail.visible ?? 1) !== 0,
  };
}

function filterAvailableMenus(items: MenuRecord[], currentId?: string) {
  return items.reduce<MenuRecord[]>((result, item) => {
    if (currentId && String(item.id) === String(currentId)) {
      return result;
    }
    result.push({
      ...item,
      id: String(item.id),
      parentId: item.parentId === null || item.parentId === undefined ? null : String(item.parentId),
      children: filterAvailableMenus(item.children ?? [], currentId),
    });
    return result;
  }, []);
}

const { formData, loading, submitLoading } = toRefs(dataInfo);
</script>
