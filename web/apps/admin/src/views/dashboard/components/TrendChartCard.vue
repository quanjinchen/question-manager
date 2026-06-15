<template>
  <section class="TrendChartCard-root surface-card">
    <header class="card-head">
      <div>
        <h3>{{ title }}</h3>
        <p>{{ subtitle }}</p>
      </div>
      <el-radio-group v-if="safeTabs.length" v-model="currentValue" size="small" @change="emit('change', currentValue)">
        <el-radio-button v-for="item in safeTabs" :key="item.id" :value="item.id">{{ item.name }}</el-radio-button>
      </el-radio-group>
    </header>

    <div v-if="!series.length" class="empty-state">
      <AppEmpty />
    </div>

    <EChartPanel v-else :option="chartOption" />
  </section>
</template>

<script setup lang="ts" name="TrendChartCard">
  import { computed } from 'vue';
  import EChartPanel from './EChartPanel.vue';

  type TabItem = {
    id: string;
    name: string;
  };

  type SeriesItem = {
    name: string;
    color: string;
    values: number[];
  };

  const props = defineProps<{
    title: string;
    subtitle: string;
    labels: string[];
    tabs?: TabItem[];
    modelValue?: string;
    series: SeriesItem[];
  }>();

  const emit = defineEmits<{
    change: [value: string];
  }>();

  const currentValue = computed({
    get: () => props.modelValue ?? '',
    set: value => emit('change', value)
  });

  const safeTabs = computed(() => props.tabs ?? []);

  const chartOption = computed(() => ({
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      bottom: 0
    },
    grid: {
      left: 24,
      right: 24,
      top: 16,
      bottom: 48,
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: props.labels,
      axisTick: { show: false },
      axisLine: { lineStyle: { color: '#d0d5dd' } },
      axisLabel: { color: '#98a2b3' }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#eef2f6' } },
      axisLabel: { color: '#98a2b3' }
    },
    series: props.series.map(item => ({
      name: item.name,
      type: 'bar',
      barMaxWidth: 24,
      itemStyle: {
        borderRadius: [8, 8, 0, 0],
        color: item.color
      },
      data: item.values
    }))
  }));
</script>

<style scoped lang="scss">
  .TrendChartCard-root {
    padding: 24px;
    min-height: 320px;
  }

  .card-head {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 16px;
    margin-bottom: 20px;
  }

  .card-head h3 {
    margin: 0 0 6px;
    font-size: 18px;
    color: #1f2937;
  }

  .card-head p {
    margin: 0;
    color: #667085;
  }

  .empty-state {
    min-height: 220px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

</style>
