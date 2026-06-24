<template>
  <main class="CategoryList-root">
    <header class="page-header">
      <div>
        <h1>我的题库</h1>
        <p>{{ account?.fullName || account?.username || '答题用户' }}</p>
      </div>
      <van-button size="small" plain type="primary" @click="dataInfo.logout()">退出</van-button>
    </header>

    <van-loading v-if="loading" class="state" />
    <section v-else-if="!list.length" class="empty-state">
      <van-icon name="records-o" />
      <p>暂无题库</p>
    </section>
    <section v-else class="category-grid">
      <button v-for="item in list" :key="item.id" class="category-card" type="button" @click="router.push(`/answer/${item.id}`)">
        <span class="category-title">{{ item.categoryName }}</span>
        <span class="category-desc">{{ item.description || '点击开始答题' }}</span>
        <span class="category-meta">{{ item.questionCount || 0 }} 道题</span>
      </button>
    </section>
  </main>
</template>

<script setup lang="ts" name="CategoryList">
import { reactive, toRefs } from 'vue';
import { useRouter } from 'vue-router';
import { $apis } from '@/apis/requests';
import { clearCurrentAuthAccount, getCurrentAuthAccount } from '@/apis/auth-session';

const router = useRouter();

const dataInfo: any = reactive({
  account: getCurrentAuthAccount(),
  list: [] as Array<Record<string, any>>,
  loading: false,
  async getList() {
    this.loading = true;
    try {
      this.list = await $apis.questionPortal.listMyCategory();
    } finally {
      this.loading = false;
    }
  },
  async logout() {
    try {
      await $apis.questionPortal.logout();
    } finally {
      clearCurrentAuthAccount();
      await router.replace('/login');
    }
  },
});

const { account, list, loading } = toRefs(dataInfo);
dataInfo.getList();
</script>

<style scoped lang="scss">
.CategoryList-root {
  min-height: 100vh;
  padding: 20px;
  background: #f5f7fb;
}

.page-header {
  width: min(960px, 100%);
  margin: 0 auto 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header h1 {
  margin: 0;
  font-size: 24px;
  letter-spacing: 0;
}

.page-header p {
  margin: 4px 0 0;
  color: #6b7280;
}

.category-grid {
  width: min(960px, 100%);
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 12px;
}

.category-card {
  min-height: 132px;
  padding: 18px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #fff;
  text-align: left;
}

.category-title,
.category-desc,
.category-meta {
  display: block;
}

.category-title {
  color: #111827;
  font-size: 18px;
  font-weight: 600;
}

.category-desc {
  margin-top: 10px;
  color: #6b7280;
  line-height: 1.5;
}

.category-meta {
  margin-top: 14px;
  color: #0f766e;
  font-size: 13px;
}

.state {
  display: block;
  margin: 80px auto;
}

.empty-state {
  width: min(960px, 100%);
  min-height: 260px;
  margin: 0 auto;
  display: grid;
  place-items: center;
  align-content: center;
  gap: 10px;
  border-radius: 8px;
  background: #fff;
  color: #9ca3af;
}

.empty-state :deep(.van-icon) {
  color: #9ca3af;
  font-size: 44px;
}

.empty-state p {
  margin: 0;
  color: #6b7280;
  font-size: 15px;
}
</style>
