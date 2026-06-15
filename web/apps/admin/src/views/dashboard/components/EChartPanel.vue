<template>
  <div ref="chartRef" class="EChartPanel-root"></div>
</template>

<script setup lang="ts" name="EChartPanel">
  import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue';
  import * as echarts from 'echarts';

  const props = defineProps<{
    option: Record<string, any>;
  }>();

  const chartRef = ref<HTMLElement | null>(null);
  let chartInstance: echarts.ECharts | null = null;

  function renderChart() {
    if (!chartRef.value) {
      return;
    }
    if (!chartInstance) {
      chartInstance = echarts.init(chartRef.value);
    }
    chartInstance.setOption(props.option, true);
    chartInstance.resize();
  }

  function handleResize() {
    chartInstance?.resize();
  }

  onMounted(async () => {
    await nextTick();
    renderChart();
    window.addEventListener('resize', handleResize);
  });

  watch(
    () => props.option,
    async () => {
      await nextTick();
      renderChart();
    },
    { deep: true }
  );

  onBeforeUnmount(() => {
    window.removeEventListener('resize', handleResize);
    chartInstance?.dispose();
    chartInstance = null;
  });
</script>

<style scoped lang="scss">
  .EChartPanel-root {
    width: 100%;
    height: 240px;
  }
</style>

