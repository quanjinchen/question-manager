<template>
  <el-table
    ref="tableRef"
    class="AppTable-root"
    v-loading="loading"
    v-bind="tablePropsResult"
    @selection-change="$emit('selectionChange', $event)"
  >
    <el-table-column
      v-if="tableInfo.hasSelection"
      type="selection"
      width="48"
    />

    <el-table-column
      v-for="column in tableColumns"
      :key="column.key ?? column.prop"
      v-bind="column"
    >
      <template #default="{ row, $index }">
        <!-- 页码 -->
        <template v-if="column.genre === '$ordinal'">
          {{ $utils.Tool.getOrdinalKey($index, pageInfo.pageNum, pageInfo.pageSize) }}
        </template>
        <!-- 日期 -->
        <template v-else-if="column.genre === '$date'">
          {{ $utils.Date.formatDateTime(row[column.prop], column.format) }}
        </template>
        <template v-else-if="column.genre === '$tag'">
          <el-tag :type="resolveTagType(column, row)">{{
            resolveTagText(column, row)
          }}</el-tag>
        </template>
        <template v-else-if="column.genre === '$slot'">
          <slot :name="column.prop" :row="row" :index="$index" />
        </template>
        <template v-else-if="column.genre === '$action'">
          <div class="actions">
            <AppButton
              v-for="action in resolveActions(column, row)"
              :key="action.key"
              :button-props="{ type: action.type ?? 'primary', link: true }"
              @click="$emit('handleClick', row, action, $index)"
            >
              {{
                typeof action.label === "function"
                  ? action.label(row)
                  : action.label
              }}
            </AppButton>
          </div>
        </template>
        <template v-else>
          {{ row[column.prop] || isNumber(row[column.prop]) }}
        </template>
      </template>
    </el-table-column>

    <template #empty>
      <slot name="empty">
        <AppEmpty />
      </slot>
    </template>
  </el-table>
</template>

<script setup lang="ts" name="AppTable">
import { computed, ref, useAttrs } from "vue";
import { dictStore } from "@vue-scaffold/constants";
import $utils, { hasStoredPermission } from "@vue-scaffold/utils";
import { loadingAttrs } from "../composables";
import AppButton from "./AppButton.vue";
import AppEmpty from "./AppEmpty.vue";
const props = defineProps({
  // 组件属性
  tableProps: { type: Object, default: () => ({}) },
  // 表格信息
  tableInfo: { type: Object, default: () => ({ columns: [] }) },
  // 页码页数
  pageInfo: {
    type: Object,
    default: () => ({ pageNum: 1, pageSize: 10 }),
  },
  // loading状态
  loading: {
    type: Boolean,
    default: false,
  },
});

defineEmits(["selectionChange", "handleClick"]);

const attrs = useAttrs();
const tableRef = ref();
defineExpose({
  get table() {
    return tableRef.value;
  },
});

const tablePropsResult = computed(() =>
  $utils.Object.deepAssign(
    {
      stripe: false,
      data: [],
      emptyText: "暂无数据",
    },
    loadingAttrs,
    attrs as Record<string, any>,
    props.tableProps,
  ),
);

const tableColumns = computed(() =>
  (props.tableInfo.columns ?? []).filter(
    (item: Record<string, any>) => item.show !== false,
  ),
);

function resolveTagText(column: Record<string, any>, row: Record<string, any>) {
  const item = getDictItem(column, row)
  return item?.label || ''
}

function resolveTagType(
  column: Record<string, any>,
  row: Record<string, any>,
) {
  const item = getDictItem(column, row)
  return item?.type || 'default'
}

const getDictItem = (column: Record<string, any>, row: Record<string, any>) => {
  const dictKey = column.dictKey as keyof typeof dictStore;
  const arr = (dictStore[dictKey] ?? []) as Array<Record<string, any>>;
  const item = arr.find((item: Record<string, any>) => item.value == row[column.prop]);
  return item
};

function resolveActions(column: Record<string, any>, row: Record<string, any>) {
  return (column.actions ?? []).filter((action: Record<string, any>) => {
    const visibleByPermission = hasStoredPermission(action.permissions);
    const visibleByPredicate =
      typeof action.visible === "function"
        ? action.visible(row)
        : action.visible !== false;
    return visibleByPermission && visibleByPredicate;
  });
}

// 是否数字
const isNumber = computed(() => {
  return (value: unknown) => {
    return typeof value === "number" && !isNaN(value) ? value : "-";
  };
});
</script>

<style scoped lang="scss">
.AppTable-root {
  :deep(.el-table__header th) {
    background: #f4f6fb !important;
    color: #58627a;
    font-weight: 500;
  }
}

.actions {
  display: inline-flex;
  gap: 4px;
  flex-wrap: wrap;
}
</style>
