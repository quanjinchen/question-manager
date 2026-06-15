<template>
  <section class="RankListCard-root surface-card">
    <header class="card-head">
      <div>
        <h3>{{ title }}</h3>
        <p>{{ subtitle }}</p>
      </div>
      <el-radio-group v-model="currentValue" size="small" @change="emit('change', currentValue)">
        <el-radio-button v-for="item in tabs" :key="item.id" :value="item.id">{{ item.name }}</el-radio-button>
      </el-radio-group>
    </header>

    <div v-if="!list.length" class="empty-state">
      <AppEmpty />
    </div>

    <EChartPanel v-else :option="chartOption" />
  </section>
</template>

<script setup lang="ts" name="RankListCard">
  import { computed } from 'vue';
  import EChartPanel from './EChartPanel.vue';

  type RankItem = {
    name: string;
    num: number;
  };

  type TabItem = {
    id: string;
    name: string;
  };

  const props = defineProps<{
    title: string;
    subtitle: string;
    list: RankItem[];
    tabs: TabItem[];
    modelValue: string;
    unitLabel: string;
  }>();

  const emit = defineEmits<{
    change: [value: string];
  }>();

  const currentValue = computed({
    get: () => props.modelValue,
    set: value => emit('change', value)
  });

  const chartOption = computed(() => ({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: 24,
      right: 24,
      top: 16,
      bottom: 24,
      containLabel: true
    },
    xAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#eef2f6' } },
      axisLabel: { color: '#98a2b3' }
    },
    yAxis: {
      type: 'category',
      data: props.list.map(item => item.name),
      axisTick: { show: false },
      axisLine: { show: false },
      axisLabel: { color: '#344054' }
    },
    series: [
      {
        name: props.unitLabel,
        type: 'bar',
        barMaxWidth: 18,
        itemStyle: {
          borderRadius: [0, 8, 8, 0],
          color: '#2d6df6'
        },
        data: props.list.map(item => item.num)
      }
    ]
  }));
</script>

<style scoped lang="scss">
  .RankListCard-root {
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
