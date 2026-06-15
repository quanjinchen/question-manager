<template>
  <main class="Dashboard">
    <BaseStatsCard :items="dashboard.cards" />

    <section class="dashboard-grid">
      <TrendChartCard
        title="操作用户统计"
        subtitle="按周期统计活跃操作人数量"
        :tabs="timeTabs"
        :model-value="userDataType"
        :labels="userStats.labels"
        :series="userStats.series"
        @change="userDataType = $event"
      />

      <TrendChartCard
        title="操作活跃统计"
        subtitle="同时观察活跃操作人与日志次数变化"
        :tabs="timeTabs"
        :model-value="activeDataType"
        :labels="activeStats.labels"
        :series="activeStats.series"
        @change="activeDataType = $event"
      />

      <RankListCard
        title="模块操作排行"
        subtitle="按总次数或成功次数查看模块活跃度"
        :list="rankList"
        :tabs="rankTabs"
        :model-value="rankType"
        :unit-label="rankType === 'TOTAL' ? '操作次数' : '成功次数'"
        @change="rankType = $event"
      />

      <PieSummaryCard
        title="日志分布统计"
        subtitle="查看结果分布或模块占比"
        :list="pieList"
        :total="pieTotal"
        :tabs="pieTabs"
        :model-value="pieType"
        @change="pieType = $event"
      />
    </section>
  </main>
</template>

<script setup lang="ts" name="Dashboard">
import { computed, onMounted, reactive, ref } from "vue";
import dayjs from "dayjs";
import { $apis } from "@/api/requests";
import BaseStatsCard from "@/views/dashboard/components/BaseStatsCard.vue";
import TrendChartCard from "@/views/dashboard/components/TrendChartCard.vue";
import RankListCard from "@/views/dashboard/components/RankListCard.vue";
import PieSummaryCard from "@/views/dashboard/components/PieSummaryCard.vue";

type StatsPoint = {
  date: string;
  num: number;
};

type DashboardCardItem = {
  id: number;
  name: string;
  num: number;
  subLabel: string;
  subValue: number;
  icon: string;
};

type DashboardRankItem = {
  name: string;
  num: number;
};

type DashboardPieItem = {
  name: string;
  num: number;
};

type RangeType = "WEEK" | "MONTH" | "YEAR";
type RankType = "TOTAL" | "SUCCESS";
type PieType = "STATUS" | "MODULE";

type DashboardState = {
  cards: DashboardCardItem[];
  userSeries: Record<RangeType, StatsPoint[]>;
  activeSeries: Record<
    RangeType,
    { peopleList: StatsPoint[]; timesList: StatsPoint[] }
  >;
  rankList: Record<RankType, DashboardRankItem[]>;
  pieSummary: Record<PieType, DashboardPieItem[]>;
};

const timeTabs = [
  { id: "WEEK", name: "近一周" },
  { id: "MONTH", name: "近一月" },
  { id: "YEAR", name: "近一年" },
];

const rankTabs = [
  { id: "TOTAL", name: "总次数" },
  { id: "SUCCESS", name: "成功次数" },
];

const pieTabs = [
  { id: "STATUS", name: "结果分布" },
  { id: "MODULE", name: "模块占比" },
];

const userDataType = ref("YEAR");
const activeDataType = ref("YEAR");
const rankType = ref("TOTAL");
const pieType = ref("STATUS");
const lastUpdated = ref(dayjs().format("YYYY-MM-DD HH:mm:ss"));

const dashboard = reactive<DashboardState>({
  cards: [],
  userSeries: {
    WEEK: [],
    MONTH: [],
    YEAR: [],
  },
  activeSeries: {
    WEEK: { peopleList: [], timesList: [] },
    MONTH: { peopleList: [], timesList: [] },
    YEAR: { peopleList: [], timesList: [] },
  },
  rankList: {
    TOTAL: [],
    SUCCESS: [],
  },
  pieSummary: {
    STATUS: [],
    MODULE: [],
  },
});

function createEmptyDashboard(): DashboardState {
  return {
    cards: [],
    userSeries: {
      WEEK: [],
      MONTH: [],
      YEAR: [],
    },
    activeSeries: {
      WEEK: { peopleList: [], timesList: [] },
      MONTH: { peopleList: [], timesList: [] },
      YEAR: { peopleList: [], timesList: [] },
    },
    rankList: {
      TOTAL: [],
      SUCCESS: [],
    },
    pieSummary: {
      STATUS: [],
      MODULE: [],
    },
  };
}

function normalizeStatsPointList(list: unknown): StatsPoint[] {
  if (!Array.isArray(list)) {
    return [];
  }
  return list.map((item) => ({
    date: String(item?.date ?? ""),
    num: Number(item?.num) || 0,
  }));
}

function normalizeRankItemList(list: unknown): DashboardRankItem[] {
  if (!Array.isArray(list)) {
    return [];
  }
  return list.map((item) => ({
    name: String(item?.name ?? ""),
    num: Number(item?.num) || 0,
  }));
}

function normalizePieItemList(list: unknown): DashboardPieItem[] {
  if (!Array.isArray(list)) {
    return [];
  }
  return list.map((item) => ({
    name: String(item?.name ?? ""),
    num: Number(item?.num) || 0,
  }));
}

function readBucketValue<T>(
  source: Record<string, any> | undefined,
  key: string,
  fallback: T,
): T {
  if (!source || typeof source !== "object") {
    return fallback;
  }
  return (source[key] ?? source[key.toLowerCase()] ?? fallback) as T;
}

function normalizeDashboardData(rawValue: unknown): DashboardState {
  if (!rawValue || typeof rawValue !== "object") {
    return createEmptyDashboard();
  }

  const source = rawValue as Record<string, any>;
  const userSeriesSource = source.userSeries as Record<string, any> | undefined;
  const activeSeriesSource = source.activeSeries as
    | Record<string, any>
    | undefined;
  const rankListSource = source.rankList as Record<string, any> | undefined;
  const pieSummarySource = source.pieSummary as Record<string, any> | undefined;

  return {
    cards: Array.isArray(source.cards)
      ? source.cards.map((item) => ({
          id: Number(item?.id) || 0,
          name: String(item?.name ?? ""),
          num: Number(item?.num) || 0,
          subLabel: String(item?.subLabel ?? ""),
          subValue: Number(item?.subValue) || 0,
          icon: String(item?.icon ?? ""),
        }))
      : [],
    userSeries: {
      WEEK: normalizeStatsPointList(
        readBucketValue(userSeriesSource, "WEEK", []),
      ),
      MONTH: normalizeStatsPointList(
        readBucketValue(userSeriesSource, "MONTH", []),
      ),
      YEAR: normalizeStatsPointList(
        readBucketValue(userSeriesSource, "YEAR", []),
      ),
    },
    activeSeries: {
      WEEK: {
        peopleList: normalizeStatsPointList(
          readBucketValue(
            readBucketValue(activeSeriesSource, "WEEK", {}),
            "peopleList",
            [],
          ),
        ),
        timesList: normalizeStatsPointList(
          readBucketValue(
            readBucketValue(activeSeriesSource, "WEEK", {}),
            "timesList",
            [],
          ),
        ),
      },
      MONTH: {
        peopleList: normalizeStatsPointList(
          readBucketValue(
            readBucketValue(activeSeriesSource, "MONTH", {}),
            "peopleList",
            [],
          ),
        ),
        timesList: normalizeStatsPointList(
          readBucketValue(
            readBucketValue(activeSeriesSource, "MONTH", {}),
            "timesList",
            [],
          ),
        ),
      },
      YEAR: {
        peopleList: normalizeStatsPointList(
          readBucketValue(
            readBucketValue(activeSeriesSource, "YEAR", {}),
            "peopleList",
            [],
          ),
        ),
        timesList: normalizeStatsPointList(
          readBucketValue(
            readBucketValue(activeSeriesSource, "YEAR", {}),
            "timesList",
            [],
          ),
        ),
      },
    },
    rankList: {
      TOTAL: normalizeRankItemList(
        readBucketValue(rankListSource, "TOTAL", []),
      ),
      SUCCESS: normalizeRankItemList(
        readBucketValue(rankListSource, "SUCCESS", []),
      ),
    },
    pieSummary: {
      STATUS: normalizePieItemList(
        readBucketValue(pieSummarySource, "STATUS", []),
      ),
      MODULE: normalizePieItemList(
        readBucketValue(pieSummarySource, "MODULE", []),
      ),
    },
  };
}

function applyDashboardData(nextValue: DashboardState) {
  dashboard.cards = nextValue.cards;
  dashboard.userSeries = nextValue.userSeries;
  dashboard.activeSeries = nextValue.activeSeries;
  dashboard.rankList = nextValue.rankList;
  dashboard.pieSummary = nextValue.pieSummary;
}

const userStats = computed(() => ({
  labels: dashboard.userSeries[
    userDataType.value as keyof typeof dashboard.userSeries
  ].map((item) => String(item.date)),
  series: [
    {
      name: "活跃操作人",
      color: "#6385ff",
      values: dashboard.userSeries[
        userDataType.value as keyof typeof dashboard.userSeries
      ].map((item) => Number(item.num) || 0),
    },
  ],
}));

const activeStats = computed(() => ({
  labels: dashboard.activeSeries[
    activeDataType.value as keyof typeof dashboard.activeSeries
  ].peopleList.map((item) => String(item.date)),
  series: [
    {
      name: "活跃操作人",
      color: "#22c55e",
      values: dashboard.activeSeries[
        activeDataType.value as keyof typeof dashboard.activeSeries
      ].peopleList.map((item) => Number(item.num) || 0),
    },
    {
      name: "操作次数",
      color: "#2d6df6",
      values: dashboard.activeSeries[
        activeDataType.value as keyof typeof dashboard.activeSeries
      ].timesList.map((item) => Number(item.num) || 0),
    },
  ],
}));

const rankList = computed(
  () =>
    dashboard.rankList[rankType.value as keyof typeof dashboard.rankList] ?? [],
);
const pieList = computed(
  () =>
    dashboard.pieSummary[pieType.value as keyof typeof dashboard.pieSummary] ??
    [],
);
const pieTotal = computed(() =>
  pieList.value.reduce((sum, item) => sum + (Number(item.num) || 0), 0),
);

async function init() {
  try {
    const summary = await $apis.dashboard.summary({});
    if (summary && typeof summary === "object") {
      applyDashboardData(normalizeDashboardData(summary));
    } else {
      applyDashboardData(createEmptyDashboard());
    }
  } catch {
    applyDashboardData(createEmptyDashboard());
  }
  lastUpdated.value = dayjs().format("YYYY-MM-DD HH:mm:ss");
}

onMounted(() => {
  init();
});
</script>

<style scoped lang="scss">
.Dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow-y: auto;
}

.hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 28px 32px;
  background:
    radial-gradient(
      circle at top left,
      rgba(45, 109, 246, 0.18),
      transparent 26%
    ),
    radial-gradient(
      circle at bottom right,
      rgba(34, 197, 94, 0.16),
      transparent 24%
    ),
    linear-gradient(
      135deg,
      rgba(255, 255, 255, 0.98),
      rgba(245, 249, 255, 0.94)
    );
}

.eyebrow {
  margin: 0 0 8px;
  color: #0041c0;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 12px;
}

h2 {
  margin: 0 0 10px;
  font-size: 32px;
  line-height: 1.1;
}

.description {
  margin: 0;
  max-width: 720px;
  color: #556176;
}

.hero-side {
  min-width: 220px;
  padding: 18px 20px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid #deebfb;
}

.hero-side span {
  display: block;
  color: #98a2b3;
  font-size: 12px;
  margin-bottom: 6px;
}

.hero-side strong {
  color: #111827;
  font-size: 18px;
}

.dashboard-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.dashboard-grid > * {
  flex: 1 1 calc(50% - 10px);
  min-width: 400px;
}

@media (max-width: 1080px) {
  .hero {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-side {
    min-width: 0;
    width: 100%;
  }

  .dashboard-grid > * {
    flex: 1 1 100%;
    min-width: 0;
  }
}
</style>
