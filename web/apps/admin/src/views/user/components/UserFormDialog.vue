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
        <el-col :span="24">
          <el-form-item label="人脸图片">
            <div class="face-upload">
              <div v-if="dataInfo.facePreviewUrl" class="face-preview">
                <AppImage
                  :src="dataInfo.facePreviewUrl"
                  :image-props="{ previewSrcList: [dataInfo.facePreviewUrl] }"
                />
              </div>
              <div class="face-actions">
                <input
                  ref="fileInputRef"
                  type="file"
                  accept="image/*"
                  class="face-file-input"
                  @change="dataInfo.handleFaceFileChange"
                />
                <AppButton @click="dataInfo.openFaceFilePicker()">选择图片</AppButton>
                <AppButton
                  v-if="dataInfo.facePreviewUrl || formData.faceFileId"
                  :button-props="{ type: 'danger', plain: true }"
                  @click="dataInfo.clearFaceFile()"
                >
                  清除图片
                </AppButton>
              </div>
            </div>
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="用户名" prop="username">
            <AppInput
              v-model="formData.username"
              v-trim
              placeholder="请输入用户名"
            />
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="姓名" prop="fullName">
            <AppInput
              v-model="formData.fullName"
              v-trim
              placeholder="请输入姓名"
            />
          </el-form-item>
        </el-col>

        <el-col v-if="!dataInfo.isEdit" :span="12">
          <el-form-item label="密码" prop="password">
            <div class="password-row">
              <div class="password-input-wrap">
                <AppInput
                  v-model="formData.password"
                  v-trim
                  placeholder="请输入密码"
                />
              </div>
              <AppButton @click="dataInfo.generatePassword()">随机生成</AppButton>
            </div>
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="身份证号" prop="idCard">
            <AppInput
              v-model="formData.idCard"
              v-trim
              placeholder="请输入身份证号"
            />
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="手机号" prop="phone">
            <AppInput
              v-model="formData.phone"
              v-trim
              placeholder="请输入手机号，可不填"
            />
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="邮箱" prop="email">
            <AppInput
              v-model="formData.email"
              v-trim
              placeholder="请输入邮箱，可不填"
            />
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <AppSelect
              v-model="formData.status"
              :list="dictStore.userStatusList"
              :select-props="{ placeholder: '请选择状态' }"
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

<script setup lang="ts" name="UserFormDialog">
import { computed, onBeforeUnmount, reactive, ref, toRefs, watch } from "vue";
import $utils from '@vue-scaffold/utils';
import { useVModel } from '@vue-scaffold/hooks';
import type { FormInstance, FormRules } from "element-plus";
import { $apis } from "@/api/requests";
import type { UserRecord } from "@/types/domain";
import { dictStore } from "@vue-scaffold/constants";

const ID_CARD_PATTERN =
  /^(?:[1-9]\d{5}(?:18|19|20)\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\d|3[01])\d{3}[\dXx]|[1-9]\d{5}\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\d|3[01])\d{3})$/;

const props = defineProps<{
  modelValue: boolean;
  selectItem?: UserRecord | null;
}>();

const emit = defineEmits<{
  "update:modelValue": [boolean];
  success: [];
}>();

const formRef = ref<FormInstance>();
const fileInputRef = ref<HTMLInputElement>();
const visible = useVModel(props, emit as any);

function fileToDataUrl(file: File) {
  return new Promise<string>((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(String(reader.result ?? ""));
    reader.onerror = () => reject(new Error("read file failed"));
    reader.readAsDataURL(file);
  });
}

const dataInfo: any = reactive({
  formData: {
    username: "",
    fullName: "",
    password: "",
    phone: "",
    email: "",
    idCard: "",
    faceFileId: "",
    status: 0,
    remark: "",
  },
  rules: {
    username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
    fullName: [{ required: true, message: "请输入姓名", trigger: "blur" }],
    idCard: [
      { required: true, message: "请输入身份证号", trigger: "blur" },
      {
        validator: (_rule: unknown, value: string, callback: (error?: Error) => void) => {
          if (!value) {
            callback(new Error("请输入身份证号"));
            return;
          }
          if (!ID_CARD_PATTERN.test(value)) {
            callback(new Error("身份证号格式不正确"));
            return;
          }
          callback();
        },
        trigger: "blur",
      },
    ],
    password: [
      {
        required: true,
        validator: (_rule: unknown, value: string, callback: (error?: Error) => void) => {
          if (dataInfo.isEdit) {
            callback();
            return;
          }
          if (!value) {
            callback(new Error("请输入密码"));
            return;
          }
          callback();
        },
        trigger: "blur",
      },
    ],
  } as FormRules,
  submitLoading: false,
  loading: false,
  facePreviewUrl: "",
  faceFile: null as File | null,
  faceChanged: false,
  get isEdit() {
    return Boolean(props.selectItem?.id);
  },
  initForm() {
    formRef.value?.resetFields();
    formRef.value?.clearValidate();
    this.revokeFacePreviewUrl();
    this.facePreviewUrl = "";
    this.faceFile = null;
    this.faceChanged = false;
    if (fileInputRef.value) {
      fileInputRef.value.value = "";
    }
  },
  generatePassword() {
    const chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%^&*";
    let password = "";
    for (let index = 0; index < 12; index++) {
      password += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    this.formData.password = password;
    formRef.value?.validateField?.("password");
  },
  revokeFacePreviewUrl() {
    if (this.facePreviewUrl?.startsWith?.("blob:")) {
      URL.revokeObjectURL(this.facePreviewUrl);
    }
  },
  openFaceFilePicker() {
    fileInputRef.value?.click();
  },
  handleFaceFileChange(event: Event) {
    const target = event.target as HTMLInputElement;
    const file = target.files?.[0];
    if (!file) {
      return;
    }
    this.revokeFacePreviewUrl();
    this.faceFile = file;
    this.faceChanged = true;
    this.formData.faceFileId = "";
    this.facePreviewUrl = URL.createObjectURL(file);
  },
  clearFaceFile() {
    this.revokeFacePreviewUrl();
    this.faceFile = null;
    this.faceChanged = true;
    this.facePreviewUrl = "";
    this.formData.faceFileId = "";
    if (fileInputRef.value) {
      fileInputRef.value.value = "";
    }
  },
  async getDetail() {
    if (!props.selectItem?.id) {
      return;
    }

    this.loading = true;
    try {
      const detail = await $apis.users.detail({
        id: Number(props.selectItem.id),
      });
      this.formData = {
        username: "",
        fullName: "",
        password: "",
        phone: "",
        email: "",
        idCard: "",
        faceFileId: "",
        status: 0,
        remark: "",
        ...detail,
      };
      this.faceFile = null;
      this.faceChanged = false;
      this.revokeFacePreviewUrl();
      this.facePreviewUrl = "";
      if (detail?.faceFileId) {
        try {
          const fileBlob = await $apis.files.download(detail.faceFileId, {
            alertError: false,
          });
          const blobData = fileBlob?.data instanceof Blob ? fileBlob.data : null;
          this.facePreviewUrl = blobData ? URL.createObjectURL(blobData) : "";
        } catch {
          this.facePreviewUrl = "";
        }
      }
    } finally {
      this.loading = false;
    }
  },
  get params() {
    const paramsRes: Record<string, any> = { ...dataInfo.formData };
    if (dataInfo.isEdit) {
      delete paramsRes.password;
    }
    if (!dataInfo.faceChanged) {
      delete paramsRes.faceFileId;
    } else {
      paramsRes.faceFileId = dataInfo.formData.faceFileId || null;
    }
    return paramsRes;
  },
  async handleSubmit() {
    await formRef.value?.validate();
    if (this.submitLoading) return;
    this.submitLoading = true;
    try {
      const params = this.params;
      if (this.faceChanged && this.faceFile) {
        params.faceBase64 = await fileToDataUrl(this.faceFile);
      } else if (this.faceChanged && !this.faceFile) {
        params.faceBase64 = "";
        params.faceFeature = "";
        await $utils.Message.messageConfirm('清空图片，会导致人脸特征值也清空，导致无法人脸认证，确认？')
      }
      await $apis.users[this.isEdit ? "update" : "create"]({
        ...params,
      });
      $utils.Message.messageAlert({ message: `操作成功` });
      visible.value = false;
      emit("success");
    } finally {
      this.submitLoading = false;
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
      text: submitLoading.value ? "保存中..." : "保存",
      close: false,
      buttonProps: { type: "primary", loading: submitLoading.value },
      click: () => dataInfo.handleSubmit(),
    },
  ],
}));

watch(visible, (value) => {
  !value && dataInfo.initForm();
  value && dataInfo.getDetail();
});

const { submitLoading, loading, formData } = toRefs(dataInfo);

onBeforeUnmount(() => {
  dataInfo.revokeFacePreviewUrl();
});

defineExpose({ dataInfo });
</script>

<style scoped lang="scss">
.face-upload {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.face-preview {
  width: 120px;
  height: 120px;
  overflow: hidden;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  background: #f5f7fa;
  flex-shrink: 0;
}

.face-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.password-row {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.password-input-wrap {
  flex: 1;
}

.password-input-wrap :deep(.AppInput) {
  width: 100%;
}

.face-file-input {
  display: none;
}
</style>
