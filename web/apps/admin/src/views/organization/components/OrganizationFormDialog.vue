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
      <el-form-item label="上级组织" prop="parentId">
        <el-tree-select
          v-model="formData.parentId"
          style="width: 100%"
          :data="organizationTree"
          node-key="id"
          :props="{ label: 'orgName', children: 'children' }"
          placeholder="请选择上级组织"
          clearable
          check-strictly
          default-expand-all
        />
      </el-form-item>

      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="组织名称" prop="orgName">
            <AppInput v-model="formData.orgName" v-trim placeholder="请输入组织名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="组织简称" prop="shortName">
            <AppInput v-model="formData.shortName" v-trim placeholder="请输入组织简称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="排序" prop="orderNum">
            <AppInputNumber
              v-model="formData.orderNum"
              :input-number-props="{ min: 1, max: 9999, style: 'width: 100%', placeholder: '请输入排序' }"
            />
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

<script setup lang="ts" name="OrganizationFormDialog">
import { computed, reactive, ref, toRefs, watch } from 'vue';
import $utils from '@vue-scaffold/utils';
import { useVModel } from '@vue-scaffold/hooks';
import type { FormInstance } from 'element-plus';
import { $apis } from '@/api/requests';
import type { OrganizationRecord } from '@/types/domain';

type OrganizationFormData = Omit<OrganizationRecord, 'id' | 'children'>;

const props = defineProps<{
  modelValue: boolean;
  selectItem?: OrganizationRecord | null;
  organizations: OrganizationRecord[];
}>();

const emit = defineEmits<{
  'update:modelValue': [boolean];
  success: [];
}>();

const formRef = ref<FormInstance>();
const visible = useVModel(props, emit as any);

const modalProps = computed(() => ({
  title: `${dataInfo.isEdit ? '编辑' : '新增'}组织`,
  width: 640,
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

const organizationTree = computed(() => props.organizations ?? []);

const dataInfo = reactive({
  formData: {
    parentId: null,
    orgName: '',
    shortName: '',
    orderNum: 1,
    remark: '',
  } as OrganizationFormData,
  rules: {
    orgName: [{ required: true, message: '请输入组织名称', trigger: 'blur' }],
    orderNum: [{ required: true, message: '请输入排序', trigger: 'change' }],
  },
  loading: false,
  submitLoading: false,
  get isEdit() {
    return Boolean(props.selectItem?.id);
  },
  initForm() {
    this.formData = {
      parentId: null,
      orgName: '',
      shortName: '',
      orderNum: 1,
      remark: '',
    };
    formRef.value?.clearValidate();
  },
  async getDetail() {
    if (!props.selectItem?.id) {
      if (props.selectItem?.parentId) {
        this.formData.parentId = props.selectItem.parentId;
      }
      return;
    }

    this.loading = true;
    try {
      const detail = await $apis.organizations.detail({
        id: Number(props.selectItem.id),
      });
      this.formData = mapOrganizationDetail(detail);
    } finally {
      this.loading = false;
    }
  },
  get params() {
    return {
      id: this.isEdit ? Number(props.selectItem?.id) : undefined,
      parentId: this.formData.parentId ? Number(this.formData.parentId) : 0,
      orgCode: this.formData.shortName ?? '',
      name: this.formData.orgName,
      leaderName: this.formData.remark ?? '',
      sortOrder: this.formData.orderNum,
      status: 1,
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
      await $apis.organizations[this.isEdit ? 'update' : 'save'](this.params);
      $utils.Message.messageAlert({ message: this.isEdit ? '组织更新成功' : '组织创建成功' });
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

function mapOrganizationDetail(detail: Record<string, any>): OrganizationFormData {
  return {
    parentId:
      detail.parentId === null || detail.parentId === undefined
        ? null
        : String(detail.parentId),
    orgName: detail.name ?? detail.orgName ?? '',
    shortName: detail.orgCode ?? detail.shortName ?? '',
    orderNum: Number(detail.sortOrder ?? detail.orderNum ?? 1),
    remark: detail.leaderName ?? detail.remark ?? '',
  };
}

const { formData, loading, submitLoading } = toRefs(dataInfo);
</script>
