<template>
  <article class="Account-root">
    <el-form ref="formRef" :model="form.model" :rules="form.rules" label-position="top" :validate-on-rule-change="false"
      @keyup.enter="dataInfo.login(formRef)">
      <el-form-item prop="account">
        <AppInput v-model="form.model.account" placeholder="请输入账号"
          :input-props="{ clearable: false, autocomplete: 'off' }" />
      </el-form-item>
      <el-form-item prop="password">
        <AppInput v-model="form.model.password" placeholder="请输入密码"
          :input-props="{ type: 'password', autocomplete: 'off', showPassword: true }" />
      </el-form-item>
      <el-form-item prop="code">
        <GraphVerifyCode ref="graphVerifyCodeRef" />
      </el-form-item>
      <el-form-item class="form-item-btn">
        <AppButton :button-props="{ type: 'primary', disabled: btnLoading, loading: btnLoading }"
          @click="dataInfo.login(formRef)">
          {{ !btnLoading ? '登录' : '登录中...' }}
        </AppButton>
      </el-form-item>
    </el-form>
  </article>
</template>

<script setup lang="ts" name="Account">
import { reactive, ref, toRefs } from 'vue';
import type { FormInstance } from 'element-plus';
import GraphVerifyCode from './GraphVerifyCode.vue';

const props = defineProps({
  btnLoading: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits<{
  onLogin: [
    payload: {
      params: Record<string, string>;
    }
  ];
}>();

const formRef = ref<FormInstance>();
const graphVerifyCodeRef = ref<InstanceType<typeof GraphVerifyCode>>();

const dataInfo = reactive({
  form: {
    model: {
      account: '',
      password: '',
      code: ''
    },
    rules: {
      account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
      password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
      code: [{
        required: true,
        trigger: 'blur',
        validator: (_rule: unknown, _value: string, callback: (message?: string) => void) => {
          if (!graphVerifyCodeRef.value?.dataInfo.verifyCode) {
            callback('请输入验证码');
            return;
          }
          callback();
        }
      }]
    }
  },
  changeFirstPassword() {
    dataInfo.form.model.password = '';
    dataInfo.resetCodeValue();
  },
  resetCodeValue() {
    graphVerifyCodeRef.value?.dataInfo.init();
  },
  init() {
    dataInfo.resetCodeValue();
    formRef.value?.resetFields();
    formRef.value?.clearValidate();
  },
  get params() {
    return {
      ...this.form.model,
      password: this.form.model.password,
      code: graphVerifyCodeRef.value?.dataInfo.verifyCode || '',
      uuid: graphVerifyCodeRef.value?.dataInfo.uuid || ''
    };
  },
  async login(formEl: FormInstance | undefined) {
    if (!formEl) {
      return;
    }
    try {
      await formEl.validate();
      emit('onLogin', { params: this.params });
    } catch (_error) { }
  }
});

const { form } = toRefs(dataInfo);

defineExpose({ dataInfo, formRef });
</script>

<style scoped lang="scss">
.Account-root {
  width: 100%;
  margin-top: 10px;
}

.Account-root :deep(.AppInput-root) {
  height: 44px;
}

.form-item-btn {
  margin-bottom: 0;
  margin-top: 28px;
}

.form-item-btn :deep(.AppButton-root) {
  width: 100%;
  height: 44px;
}
</style>
