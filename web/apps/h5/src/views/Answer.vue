<template>
  <main class="Answer-root">
    <van-nav-bar title="在线答题" left-text="返回" left-arrow @click-left="router.back()" />
    <van-loading v-if="loading" class="state" />
    <van-empty v-else-if="!questions.length" description="暂无可答题目" />
    <section v-else class="answer-shell">
      <div class="progress-line">
        <span>{{ currentIndex + 1 }} / {{ questions.length }}</span>
        <van-progress :percentage="progress" />
      </div>
      <article class="question-panel">
        <div class="question-heading">
          <van-tag class="question-type-tag" :class="`is-${currentQuestion.questionType}`" plain>
            {{ questionTypeMap[currentQuestion.questionType] }}
          </van-tag>
          <div class="question-title">
            <span class="question-index">{{ currentIndex + 1 }}.</span>
            <span class="question-text">{{ currentQuestion.title }}</span>
          </div>
        </div>

        <van-cell-group v-if="currentQuestion.questionType === 'SINGLE' || currentQuestion.questionType === 'JUDGE'" class="option-group">
          <van-cell
            v-for="item in currentOptions"
            :key="item.label"
            class="option-cell"
            :class="{ 'is-selected': dataInfo.isSingleSelected(item.label) }"
            clickable
            @click="dataInfo.selectSingle(item.label)"
          >
            <template #title>
              <div class="option-row">
                <van-radio
                  class="option-control"
                  :model-value="dataInfo.isSingleSelected(item.label)"
                  @click.stop="dataInfo.selectSingle(item.label)"
                />
                  <span class="option-label">{{ item.label }}.</span>
                  <span class="option-text">{{ item.content }}</span>
              </div>
            </template>
          </van-cell>
        </van-cell-group>

        <van-cell-group v-else-if="currentQuestion.questionType === 'MULTIPLE'" class="option-group">
          <van-cell
            v-for="item in currentOptions"
            :key="item.label"
            class="option-cell"
            :class="{ 'is-selected': dataInfo.isMultiSelected(item.label) }"
            clickable
            @click="dataInfo.toggleMulti(item.label)"
          >
            <template #title>
              <div class="option-row">
                <van-checkbox
                  class="option-control"
                  :model-value="dataInfo.isMultiSelected(item.label)"
                  shape="square"
                  @click.stop="dataInfo.toggleMulti(item.label)"
                />
                <span class="option-label">{{ item.label }}.</span>
                <span class="option-text">{{ item.content }}</span>
              </div>
            </template>
          </van-cell>
        </van-cell-group>

        <van-field v-else v-model="answerMap[currentQuestion.id]" rows="6" autosize type="textarea" placeholder="请输入问答题答案" />
      </article>

      <footer class="answer-actions">
        <van-button plain type="primary" @click="showQuestionDrawer = true">题卡</van-button>
        <van-button plain type="danger" @click="dataInfo.finishAnswer()">结束答题</van-button>
        <van-button :disabled="currentIndex === 0" @click="currentIndex--">上一题</van-button>
        <van-button v-if="currentIndex < questions.length - 1" type="primary" @click="currentIndex++">下一题</van-button>
        <van-button v-else type="primary" :loading="submitLoading" @click="dataInfo.submit()">提交</van-button>
      </footer>

      <van-popup v-model:show="showQuestionDrawer" position="bottom" round class="question-drawer">
        <section class="drawer-inner">
          <header class="drawer-header">
            <div>
              <h2>题卡</h2>
              <p>{{ answeredCount }} / {{ questions.length }} 已作答</p>
            </div>
            <van-button size="small" plain @click="showQuestionDrawer = false">关闭</van-button>
          </header>

          <div class="question-type-list">
            <section v-for="group in groupedQuestions" :key="group.type" class="question-type-section">
              <h3>{{ group.label }} <span>{{ group.items.length }} 道</span></h3>
              <div class="question-grid">
                <button
                  v-for="item in group.items"
                  :key="item.question.id"
                  class="question-number"
                  :class="{
                    'is-current': item.index === currentIndex,
                    'is-answered': dataInfo.isAnswered(item.question),
                  }"
                  type="button"
                  @click="dataInfo.goQuestion(item.index)"
                >
                  {{ item.index + 1 }}
                </button>
              </div>
            </section>
          </div>
        </section>
      </van-popup>
    </section>
  </main>
</template>

<script setup lang="ts" name="Answer">
import { computed, reactive, toRefs } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { showConfirmDialog, showToast } from 'vant';
import { $apis } from '@/apis/requests';

const route = useRoute();
const router = useRouter();
const questionTypeMap: Record<string, string> = { SINGLE: '单选', MULTIPLE: '多选', JUDGE: '判断', QA: '问答' };

const dataInfo: any = reactive({
  categoryId: Number(route.params.categoryId),
  questions: [] as Array<Record<string, any>>,
  currentIndex: 0,
  answerMap: {} as Record<string, string>,
  multiAnswerMap: {} as Record<string, string[]>,
  showQuestionDrawer: false,
  loading: false,
  submitLoading: false,
  get currentQuestion() {
    return this.questions[this.currentIndex] ?? {};
  },
  get currentOptions() {
    try {
      return this.currentQuestion.optionsJson ? JSON.parse(this.currentQuestion.optionsJson) : [];
    } catch (_error) {
      return [];
    }
  },
  async getQuestions() {
    this.loading = true;
    try {
      this.questions = await $apis.questionPortal.listQuestions({ id: this.categoryId });
      this.questions.forEach((item: Record<string, any>) => {
        if (item.questionType === 'MULTIPLE') {
          this.multiAnswerMap[item.id] = [];
        }
      });
    } finally {
      this.loading = false;
    }
  },
  toggleMulti(label: string) {
    const id = this.currentQuestion.id;
    const values = this.multiAnswerMap[id] || [];
    this.multiAnswerMap[id] = values.includes(label)
      ? values.filter((item: string) => item !== label)
      : [...values, label];
  },
  isSingleSelected(label: string) {
    return this.answerMap[this.currentQuestion.id] === label;
  },
  selectSingle(label: string) {
    this.answerMap[this.currentQuestion.id] = label;
  },
  isMultiSelected(label: string) {
    return (this.multiAnswerMap[this.currentQuestion.id] || []).includes(label);
  },
  getQuestionAnswer(question: Record<string, any>) {
    return question.questionType === 'MULTIPLE'
      ? (this.multiAnswerMap[question.id] || []).join(',')
      : (this.answerMap[question.id] || '');
  },
  isAnswered(question: Record<string, any>) {
    return Boolean(this.getQuestionAnswer(question));
  },
  goQuestion(index: number) {
    this.currentIndex = index;
    this.showQuestionDrawer = false;
  },
  buildAnswers() {
    return this.questions.map((item: Record<string, any>) => ({
      questionId: Number(item.id),
      userAnswer: this.getQuestionAnswer(item),
    }));
  },
  async submit() {
    const answers = this.buildAnswers();
    const emptyCount = answers.filter((item: Record<string, any>) => !item.userAnswer).length;
    if (emptyCount > 0) {
      showToast(`还有 ${emptyCount} 道题未答`);
      return;
    }
    await showConfirmDialog({ title: '提交答卷', message: '确认提交本次答题吗？' });
    this.submitLoading = true;
    try {
      const data = await $apis.questionPortal.submitAnswer({
        categoryId: this.categoryId,
        answers,
      });
      await router.replace(`/score/${data.id}`);
    } finally {
      this.submitLoading = false;
    }
  },
  async finishAnswer() {
    await showConfirmDialog({ title: '结束答题', message: '确认结束本次答题吗？当前答案不会提交。' });
    await router.replace('/categories');
  },
});

const progress = computed(() => Math.round(((dataInfo.currentIndex + 1) / Math.max(dataInfo.questions.length, 1)) * 100));
const answeredCount = computed(() => dataInfo.questions.filter((item: Record<string, any>) => dataInfo.isAnswered(item)).length);
const groupedQuestions = computed(() => {
  const groupMap: Record<string, Array<{ question: Record<string, any>; index: number }>> = {};
  dataInfo.questions.forEach((item: Record<string, any>, index: number) => {
    if (!groupMap[item.questionType]) {
      groupMap[item.questionType] = [];
    }
    groupMap[item.questionType].push({ question: item, index });
  });

  return Object.keys(groupMap).map(type => ({
    type,
    label: questionTypeMap[type] || type,
    items: groupMap[type],
  }));
});
const { questions, currentIndex, answerMap, multiAnswerMap, showQuestionDrawer, loading, submitLoading } = toRefs(dataInfo);
const currentQuestion = computed(() => dataInfo.currentQuestion);
const currentOptions = computed(() => dataInfo.currentOptions);
dataInfo.getQuestions();
</script>

<style scoped lang="scss">
.Answer-root {
  min-height: 100vh;
  background: #f5f7fb;
}

.answer-shell {
  width: min(900px, 100%);
  margin: 0 auto;
  padding: 16px;
}

.progress-line {
  display: grid;
  gap: 8px;
  margin-bottom: 12px;
  color: #4b5563;
}

.question-panel {
  padding: 18px;
  border-radius: 8px;
  background: #fff;
}

.question-heading {
  margin-bottom: 16px;
}

.question-title {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 8px;
  align-items: flex-start;
  margin-top: 8px;
  color: #111827;
  font-size: 17px;
  line-height: 1.6;
}

.question-type-tag {
  padding: 3px 8px;
  border: 0;
  border-radius: 6px;
  font-size: 12px;
  line-height: 1.3;
  white-space: nowrap;
}

.question-type-tag.is-SINGLE {
  background: #eaf5ff;
  color: #1989fa;
}

.question-type-tag.is-MULTIPLE {
  background: #f0f7e8;
  color: #4f8a10;
}

.question-type-tag.is-JUDGE {
  background: #fff4e6;
  color: #d46b08;
}

.question-type-tag.is-QA {
  background: #f4efff;
  color: #7c3aed;
}

.question-index {
  color: #111827;
  font-weight: 700;
}

.question-text {
  min-width: 0;
  overflow-wrap: anywhere;
  word-break: break-word;
}

.option-group {
  overflow: hidden;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
}

.option-cell {
  transition: background-color 0.2s ease, box-shadow 0.2s ease;
}

.option-cell.is-selected {
  background: #eef8ff;
  box-shadow: inset 4px 0 0 #1989fa;
}

.option-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  min-height: 28px;
}

.option-control {
  flex: 0 0 auto;
}

.option-label {
  flex: 0 0 22px;
  color: #111827;
  line-height: 1.5;
  text-align: right;
}

.option-text {
  flex: 1 1 0;
  min-width: 0;
  color: #111827;
  line-height: 1.5;
  overflow-wrap: anywhere;
  word-break: break-word;
}

.answer-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 16px;
}

.question-drawer {
  max-height: 78vh;
}

.drawer-inner {
  padding: 18px 16px 24px;
}

.drawer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.drawer-header h2 {
  margin: 0 0 4px;
  color: #111827;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 0;
}

.drawer-header p {
  margin: 0;
  color: #6b7280;
  font-size: 13px;
}

.question-type-list {
  display: grid;
  gap: 18px;
}

.question-type-section h3 {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin: 0 0 10px;
  color: #111827;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 0;
}

.question-type-section h3 span {
  color: #6b7280;
  font-size: 12px;
  font-weight: 400;
}

.question-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(42px, 1fr));
  gap: 10px;
}

.question-number {
  width: 100%;
  aspect-ratio: 1;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: #fff;
  color: #374151;
  font-size: 15px;
  font-weight: 600;
}

.question-number.is-answered {
  border-color: #1989fa;
  background: #eaf5ff;
  color: #1989fa;
}

.question-number.is-current {
  border-color: #07c160;
  background: #07c160;
  color: #fff;
}

.state {
  display: block;
  margin: 80px auto;
}
</style>
