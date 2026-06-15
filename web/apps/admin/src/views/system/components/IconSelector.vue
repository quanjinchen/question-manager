<template>
  <el-popover
    v-model:visible="visible"
    placement="bottom-start"
    :width="480"
    trigger="click"
  >
    <template #reference>
      <el-input
        :model-value="modelValue"
        placeholder="请选择图标"
        readonly
        clearable
        @clear="handleClear"
      >
        <template #prefix>
          <AppIcon v-if="modelValue" :name="modelValue" />
        </template>
      </el-input>
    </template>
    <div class="icon-selector">
      <el-input
      v-model="searchText"
      placeholder="搜索图标"
    clearable
        class="search-input"
      />
      <el-scrollbar height="300px">
        <div class="icon-list">
          <div
          v-for="icon in filteredIcons"
            :key="icon"
            class="icon-item"
            :class="{ active: modelValue === icon }"
            @click="handleSelect(icon)"
          >
        <AppIcon :name="icon" />
            <span class="icon-name">{{ icon }}</span>
          </div>
        </div>
      </el-scrollbar>
    </div>
  </el-popover>
</template>

<script setup lang="ts" name="IconSelector">
import { ref, computed } from 'vue';
import * as ElementPlusIcons from '@element-plus/icons-vue';
import { AppIcon } from '@vue-scaffold/pc-ui';

const props = defineProps<{
  modelValue?: string;
}>();

const emit = defineEmits<{
  'update:modelValue': [value: string];
}>();

const visible = ref(false);
const searchText = ref('');

const allIcons = Object.keys(ElementPlusIcons);

const filteredIcons = computed(() => {
  if (!searchText.value) {
    return allIcons;
  }
  const search = searchText.value.toLowerCase();
  return allIcons.filter(icon => icon.toLowerCase().includes(search));
});

function handleSelect(icon: string) {
  emit('update:modelValue', icon);
  visible.value = false;
  searchText.value = '';
}

function handleClear() {
  emit('update:modelValue', '');
}
</script>

<style scoped lang="scss">
.icon-selector {
  .search-input {
    margin-bottom: 12px;
  }

  .icon-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  .icon-item {
    display: flex;
    flex-direction: column;
    align-items: center;
  justify-content: center;
    width: 80px;
    height: 80px;
    border: 1px solid var(--el-border-color);
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      border-color: var(--el-color-primary);
      background-color: var(--el-color-primary-light-9);
    }

    &.active {
      border-color: var(--el-color-primary);
      background-color: var(--el-color-primary-light-8);
    }

    .el-icon {
      font-size: 24px;
      margin-bottom: 4px;
    }

    .icon-name {
      font-size: 12px;
      color: var(--el-text-color-secondary);
      text-align: center;
      word-break: break-all;
      line-height: 1.2;
      max-width: 100%;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }
  }
}
</style>
