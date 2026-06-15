<template>
  <section class="BaseStatsCard-root surface-card">
    <header class="card-head">
      <h3>基础信息统计</h3>
      <p>核心接入规模与近 7 天变化</p>
    </header>
    <div class="card-grid">
      <article v-for="item in props.items" :key="item.id" class="stat-card" :class="`stat-card-${item.id}`">
        <div class="stat-copy">
          <p class="label">{{ item.name }}</p>
          <strong>{{ $utils.Tool.formatStatisticNumber(item.num) }}</strong>
          <p class="trend">
            <span>{{ item.subLabel }}</span>
            <em>{{ $utils.Tool.formatStatisticNumber(item.subValue) }}</em>
          </p>
        </div>
        <div class="stat-icon">
          <AppIcon :name="item.icon" />
        </div>
      </article>
    </div>
  </section>
</template>

<script setup lang="ts" name="BaseStatsCard">
import $utils from '@vue-scaffold/utils';

  const props = defineProps<{
    items: Array<{
      id: number | string;
      name: string;
      num: number;
      subLabel: string;
      subValue: number;
      icon: string;
    }>;
  }>();
</script>

<style scoped lang="scss">
  .BaseStatsCard-root {
    padding: 24px;
  }

  .card-head {
    margin-bottom: 18px;
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

  .card-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
  }

  .stat-card {
    flex: 1 1 calc(33.333% - 11px);
    min-width: 200px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    padding: 20px;
    border-radius: 16px;
    background: linear-gradient(135deg, #f8fbff 0%, #eef4ff 100%);
    border: 1px solid #dfe8f5;
  }

  .stat-card-2 {
    background: linear-gradient(135deg, #f2fff9 0%, #e7faf0 100%);
  }

  .stat-card-3 {
    background: linear-gradient(135deg, #f7f8ff 0%, #eef0ff 100%);
  }

  .label {
    margin: 0 0 8px;
    color: #475467;
  }

  strong {
    display: block;
    font-size: 34px;
    line-height: 1.1;
    margin-bottom: 10px;
    color: #111827;
  }

  .trend {
    margin: 0;
    display: flex;
    align-items: center;
    gap: 8px;
    color: #98a2b3;
    font-size: 13px;
  }

  .trend em {
    font-style: normal;
    font-weight: 600;
    color: #f04438;
  }

  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(255, 255, 255, 0.78);
    color: #0041c0;
  }

  .stat-icon :deep(.AppIcon-root) {
    font-size: 28px;
  }

  @media (max-width: 960px) {
    .card-grid {
      grid-template-columns: 1fr;
    }
  }
</style>
