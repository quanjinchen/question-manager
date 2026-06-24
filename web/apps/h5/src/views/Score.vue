<template>
  <main class="Score-root">
    <van-nav-bar title="答题成绩" left-text="题库" left-arrow @click-left="router.replace('/categories')" />
    <van-loading v-if="loading" class="state" />
    <section v-else class="score-shell">
      <div class="score-summary">
        <span class="score-value">{{ detail?.userScore ?? 0 }}</span>
        <span class="score-label">本次得分 / {{ detail?.totalScore ?? 0 }}</span>
        <span class="score-rate">答对 {{ detail?.correctCount ?? 0 }} / {{ detail?.questionCount ?? 0 }} 题</span>
      </div>

      <van-cell-group inset title="答题详情">
        <van-cell v-for="item in detail?.details ?? []" :key="item.questionId">
          <template #title>
            <div class="detail-title">{{ item.title }}</div>
            <div class="detail-line">你的答案：{{ item.userAnswer || '未作答' }}</div>
            <div class="detail-line">正确答案：{{ item.correctAnswer || '参考答案见解析' }}</div>
            <div v-if="item.analysis" class="detail-line">解析：{{ item.analysis }}</div>
          </template>
          <template #value>
            <van-tag :type="item.correctFlag ? 'success' : 'danger'">{{ item.userScore }} 分</van-tag>
          </template>
        </van-cell>
      </van-cell-group>

      <div class="score-actions">
        <van-button block type="primary" @click="router.replace('/categories')">返回题库</van-button>
      </div>
    </section>
  </main>
</template>

<script setup lang="ts" name="Score">
import { reactive, toRefs } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { $apis } from '@/apis/requests';

const route = useRoute();
const router = useRouter();

const dataInfo: any = reactive({
  detail: null as Record<string, any> | null,
  loading: false,
  async getDetail() {
    this.loading = true;
    try {
      this.detail = await $apis.questionPortal.recordDetail({ id: Number(route.params.recordId) });
    } finally {
      this.loading = false;
    }
  },
});

const { detail, loading } = toRefs(dataInfo);
dataInfo.getDetail();
</script>

<style scoped lang="scss">
.Score-root {
  min-height: 100vh;
  background: #f5f7fb;
}

.score-shell {
  width: min(900px, 100%);
  margin: 0 auto;
  padding: 16px;
}

.score-summary {
  display: grid;
  place-items: center;
  gap: 6px;
  margin-bottom: 16px;
  padding: 28px;
  border-radius: 8px;
  background: #fff;
}

.score-value {
  color: #0f766e;
  font-size: 48px;
  font-weight: 700;
  line-height: 1;
}

.score-label,
.score-rate,
.detail-line {
  color: #6b7280;
}

.detail-title {
  margin-bottom: 8px;
  color: #111827;
  font-weight: 600;
  line-height: 1.5;
}

.detail-line {
  margin-top: 4px;
  line-height: 1.5;
}

.score-actions {
  margin-top: 16px;
}

.state {
  display: block;
  margin: 80px auto;
}
</style>

