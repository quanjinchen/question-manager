<template>
  <section class="Home">
    <section class="stats">
      <article v-for="item in statCards" :key="item.label" class="stat surface-card">
        <p class="label">{{ item.label }}</p>
        <strong>{{ item.value }}</strong>
        <span>{{ item.note }}</span>
      </article>
    </section>
  </section>
</template>

<script setup lang="ts" name="Home">
  const statCards = [
    { label: 'Workspace', value: 'Monorepo', note: 'apps + packages' },
    { label: 'Routing', value: 'Dynamic', note: 'permission-aware guards' },
    { label: 'State', value: 'Pinia', note: 'persistence and reset helpers' },
    { label: 'API', value: 'AppRequest', note: 'request registry pattern' }
  ];
</script>

<style scoped lang="scss">
  .Home {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .hero,
  .stat {
    padding: 24px;
  }

  .hero {
    background:
      radial-gradient(circle at right top, rgba(0, 65, 192, 0.14), transparent 24%),
      linear-gradient(135deg, rgba(255, 255, 255, 0.98), rgba(246, 250, 255, 0.94));
  }

  .eyebrow {
    margin: 0 0 10px;
    color: #0041c0;
    text-transform: uppercase;
    letter-spacing: 0.12em;
    font-size: 12px;
  }

  h2 {
    margin: 0 0 10px;
    font-size: 30px;
  }

  .stats {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
  }

  .stat {
    flex: 1 1 180px;
    min-width: 180px;
  }

  .label {
    color: #667085;
    margin-bottom: 8px;
  }

  strong {
    display: block;
    font-size: 28px;
    margin-bottom: 8px;
  }

  span {
    color: #475467;
  }
</style>

