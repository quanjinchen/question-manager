<!-- pagination 分页器
  https://element-plus.gitee.io/zh-CN/component/pagination.html
-->
<template>
  <el-pagination
    v-model:page-size="pageSizeModel"
    v-model:current-page="pageIndexModel"
    class="AppPager-root"
    v-bind="paginationPropsResult"
    :total="total"
  >
  </el-pagination>
</template>
<script lang="ts" setup name="AppPager">
import { computed, watch, toRefs, useAttrs } from "vue";
import { useVModel } from "@vue-scaffold/hooks";
import $utils from "@vue-scaffold/utils";

const props = defineProps({
  paginationProps: {
    type: Object,
    default() {
      return {};
    },
  },
  pageSize: { type: Number, default: 10 },
  pageIndex: { type: Number, default: 1 },
  total: { type: Number, default: 0 },
  isSmall: { type: Boolean, default: false },
});

const attrs = useAttrs();
const emit = defineEmits<{
  (e: "change", value: { pageNum: number; pageSize: number }): void;
  (e: "update:pageSize", value: number): void;
  (e: "update:pageIndex", value: number): void;
}>();
const { pageSize, pageIndex, total, isSmall } = toRefs(props);

const pageSizeModel = useVModel(props, emit as any, "pageSize");
const pageIndexModel = useVModel(props, emit as any, "pageIndex");

const layoutResult = computed(() => {
  let layout = "prev,pager,next,sizes,jumper,->,total";
  layout =
    total.value > pageSize.value ? layout : "prev,pager,next,sizes,->,total";
  layout = !isSmall.value ? layout : layout.replace("sizes,", "");
  layout = !isSmall.value ? layout : layout.replace("jumper,", "");
  return layout;
});

const distance = !isSmall.value ? "10px" : "5px";

watch([pageSize, pageIndex], (newValues) => {
  const maxPageIndex = Math.ceil(total.value / newValues[0]);
  if (newValues[1] > maxPageIndex) {
    return;
  }
  emit("change", { pageNum: pageIndex.value, pageSize: pageSize.value });
});

const paginationPropsResult = computed(() => {
  return $utils.Object.deepAssign(
    {
      background: true,
      pageSizes: [10, 20, 30, 40, 50, 80, 100],
      layout: layoutResult.value,
      size: "default",
    },
    attrs,
    props.paginationProps,
  );
});
</script>

<style lang="scss">
.AppPager-root {
  &.el-pagination--small {
    padding: 5px 0 !important;
    .el-pagination__jump {
      margin-left: 8px !important;
    }
  }
}
</style>
<style scoped lang="scss">
.AppPager-root :deep() {
  padding: 15px 0;
  justify-content: flex-end;
  .el-pagination__jump {
    margin: 0;
    margin-left: v-bind(distance);
  }
  .el-pagination__sizes {
    margin-left: 5px;
    .el-select {
      width: 108px;
    }
  }
  .el-pagination__rightwrapper {
    flex: none;
  }
}
</style>
