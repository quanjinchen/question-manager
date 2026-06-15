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
          <el-form-item label="应用名称" prop="appName">
            <AppInput
              v-model="formData.appName"
              v-trim
              placeholder="请输入应用名称"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="应用编码" prop="appCode">
            <AppInput
              v-model="formData.appCode"
              v-trim
              placeholder="请输入应用编码"
            />
          </el-form-item>
        </el-col>
        <el-col v-if="dataInfo.isEdit" :span="12">
          <el-form-item label="Client ID">
            <AppInput
              v-model="formData.clientId"
              placeholder="系统生成"
              :input-props="{ disabled: true }"
            />
          </el-form-item>
        </el-col>
        <el-col v-if="dataInfo.isEdit" :span="12">
          <el-form-item label="Client Secret">
            <AppInput
              v-model="formData.clientSecret"
              placeholder="系统生成"
              :input-props="{ disabled: true, type: 'textarea', rows: 2 }"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="允许人脸自注册" prop="allowFaceSelfRegister">
            <AppSwitch
              v-model="formData.allowFaceSelfRegister"
              :switch-props="{
                activeText: '允许',
                inactiveText: '不允许',
              }"
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

<script setup lang="ts" name="AppFormDialog">
import { computed, reactive, ref, toRefs, watch } from 'vue';
import $utils from '@vue-scaffold/utils';
import { useVModel } from '@vue-scaffold/hooks';
import type { FormInstance } from 'element-plus';
import { $apis } from '@/api/requests';
import type { AppRecord } from '@/types/domain';

type AppFormData = Pick<AppRecord, 'appName' | 'appCode' | 'clientId' | 'clientSecret' | 'allowFaceSelfRegister' | 'remark'>;

const props = defineProps<{
  modelValue: boolean;
  selectItem?: AppRecord | null;
}>();

const emit = defineEmits<{
  'update:modelValue': [boolean];
  success: [];
}>();

const formRef = ref<FormInstance>();
const visible = useVModel(props, emit as any);

const modalProps = computed(() => ({
  title: `${dataInfo.isEdit ? '编辑' : '新增'}应用`,
  width: 720,
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

const dataInfo = reactive({
  formData: {
    appName: '',
    appCode: '',
    clientId: '',
    clientSecret: '',
    allowFaceSelfRegister: false,
    remark: '',
  } as AppFormData,
  rules: {
    appName: [{ required: true, message: '请输入应用名称', trigger: 'blur' }],
    appCode: [{ required: true, message: '请输入应用编码', trigger: 'blur' }],
  },
  loading: false,
  submitLoading: false,
  get isEdit() {
    return Boolean(props.selectItem?.id);
  },
  initForm() {
    this.formData = {
      appName: '',
      appCode: '',
      clientId: '',
      clientSecret: '',
      allowFaceSelfRegister: false,
      remark: '',
    };
    formRef.value?.clearValidate();
  },
  async getDetail() {
    if (!props.selectItem?.id) {
      return;
    }

    this.loading = true;
    try {
      const detail = await $apis.apps.detail({
        id: Number(props.selectItem.id),
      });
      this.formData = {
        appName: detail?.appName ?? '',
        appCode: detail?.appCode ?? '',
        clientId: detail?.clientId ?? '',
        clientSecret: detail?.clientSecret ?? '',
        allowFaceSelfRegister: Boolean(detail?.allowFaceSelfRegister),
        remark: detail?.remark ?? '',
      };
    } finally {
      this.loading = false;
    }
  },
  get params() {
    return {
      id: this.isEdit ? Number(props.selectItem?.id) : undefined,
      appName: this.formData.appName,
      appCode: this.formData.appCode,
      allowFaceSelfRegister: this.formData.allowFaceSelfRegister,
      remark: this.formData.remark ?? '',
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
      await $apis.apps[this.isEdit ? 'update' : 'create'](this.params);
      $utils.Message.messageAlert({ message: this.isEdit ? '应用更新成功' : '应用创建成功' });
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

const { formData, loading, submitLoading } = toRefs(dataInfo);
</script>
