<template>
  <section class="PieSummaryCard-root surface-card">
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

    <div v-else class="summary-main">
      <EChartPanel :option="chartOption" />

      <ul class="legend-list">
        <li v-for="item in normalizedList" :key="item.name">
          <div class="legend-label">
            <i :style="{ background: item.color }"></i>
            <span>{{ item.name }}</span>
          </div>
          <div class="legend-value">
            <strong>{{ item.percent }}%</strong>
            <em>{{ $utils.Tool.formatStatisticNumber(item.num) }}</em>
          </div>
        </li>
      </ul>
    </div>
  </section>
</template>

<script setup lang="ts" name="PieSummaryCard">
import { computed } from 'vue';
  import EChartPanel from './EChartPanel.vue';
import $utils from '@vue-scaffold/utils';

  type TabItem = {
    id: string;
    name: string;
  };

  type ListItem = {
    name: string;
    num: number;
  };

  const props = defineProps<{
    title: string;
    subtitle: string;
    list: ListItem[];
    total: number;
    tabs: TabItem[];
    modelValue: string;
  }>();

  const emit = defineEmits<{
    change: [value: string];
  }>();

  const colors = ['#2d6df6', '#22c55e', '#f59e0b', '#8b5cf6', '#06b6d4', '#ef4444'];

  const currentValue = computed({
    get: () => props.modelValue,
    set: value => emit('change', value)
  });

  const normalizedList = computed(() =>
    props.list.map((item, index) => ({
      ...item,
      color: colors[index % colors.length],
      percent: props.total ? Math.round((item.num / props.total) * 100) : 0
    }))
  );

  const chartOption = computed(() => ({
    tooltip: {
      trigger: 'item'
    },
    legend: {
      show: false
    },
    graphic: [
      {
        type: 'text',
        left: 'center',
        top: '42%',
        style: {
          text: '总数',
          fill: '#98a2b3',
          fontSize: 12
        }
      },
      {
        type: 'text',
        left: 'center',
        top: '52%',
        style: {
          text: String(props.total ?? 0),
          fill: '#111827',
          fontSize: 24,
          fontWeight: 700
        }
      }
    ],
    series: [
      {
        type: 'pie',
        radius: ['55%', '75%'],
        avoidLabelOverlap: true,
        label: {
          show: false
        },
        labelLine: {
          show: false
        },
        data: normalizedList.value.map(item => ({
          name: item.name,
          value: item.num,
          itemStyle: {
            color: item.color
          }
        }))
      }
    ]
  }));
</script>

<style scoped lang="scss">
  .PieSummaryCard-root {
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

  .summary-main {
    display: flex;
    gap: 24px;
    align-items: center;
  }

  .legend-list {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .legend-list li {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    padding: 12px 14px;
    border-radius: 14px;
    background: #f8fafc;
    border: 1px solid #e9eef5;
  }

  .legend-label {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    color: #344054;
  }

  .legend-label i {
    width: 10px;
    height: 10px;
    border-radius: 999px;
    display: inline-block;
  }

  .legend-value {
    text-align: right;
  }

  .legend-value strong {
    display: block;
    color: #111827;
  }

  .legend-value em {
    display: block;
    margin-top: 2px;
    font-style: normal;
    color: #98a2b3;
    font-size: 12px;
  }

  @media (max-width: 860px) {
    .summary-main {
      flex-direction: column;
    }
  }
</style>
