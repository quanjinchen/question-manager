<template>
  <main :class="['state-result', { 'state-result--fullscreen': fullscreen }]">
    <section class="state-result__panel">
      <div :class="['state-result__icon', `state-result__icon--${status}`]">
        <van-icon :name="iconName" />
      </div>
      <h1 v-if="title">{{ title }}</h1>
      <p v-if="message">{{ message }}</p>
      <div
        v-if="$slots.default"
        class="state-result__extra"
      >
        <slot />
      </div>
    </section>
  </main>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = withDefaults(defineProps<{
  status?: 'success' | 'fail' | 'warning';
  title?: string;
  message?: string;
  fullscreen?: boolean;
}>(), {
  status: 'success',
  title: '',
  message: '',
  fullscreen: true
});

const iconName = computed(() => {
  const iconMap = {
    success: 'success',
    fail: 'cross',
    warning: 'warning-o'
  };
  return iconMap[props.status];
});
</script>

<style scoped lang="scss">
.state-result {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: #f6f7fb;
}

.state-result--fullscreen {
  position: fixed;
  inset: 0;
  z-index: 1000;
}

.state-result__panel {
  width: 100%;
  padding: 0 28px 18vh;
  text-align: center;
}

.state-result__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 76px;
  height: 76px;
  border-radius: 50%;
  color: #fff;
  font-size: 44px;
}

.state-result__icon--success {
  background: #1f6feb;
}

.state-result__icon--fail {
  background: #dc2626;
}

.state-result__icon--warning {
  background: #f59e0b;
}

h1 {
  margin: 24px 0 10px;
  color: #1f2937;
  font-size: 26px;
  font-weight: 700;
}

p {
  margin: 0;
  color: #4b5563;
  font-size: 15px;
  font-weight: 700;
  line-height: 1.6;
}

.state-result__extra {
  margin-top: 28px;
}
</style>
