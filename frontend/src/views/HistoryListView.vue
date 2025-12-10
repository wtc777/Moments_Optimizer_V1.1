<template>
  <AppLayout>
    <div class="space-y-4">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-xl font-semibold text-gray-900">{{ t("historyList.title") }}</h1>
          <p class="text-sm text-gray-600">{{ t("historyList.subtitle") }}</p>
        </div>
        <LoadingSpinner v-if="loading" :message="t('historyList.loading')" />
      </div>

      <div class="rounded-lg border border-gray-200 bg-white p-4 shadow-sm">
        <label class="block text-sm font-medium text-gray-700">{{ t("historyList.userId") }}</label>
        <div class="mt-2 flex gap-2">
          <input
            v-model="userId"
            type="text"
            class="w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-indigo-500 focus:outline-none"
            :placeholder="t('historyList.userIdPlaceholder')"
          />
          <button
            class="rounded-md bg-indigo-600 px-4 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-700 disabled:cursor-not-allowed disabled:bg-indigo-400"
            :disabled="loading || !userId"
            @click="loadHistory(0)"
          >
            {{ t("historyList.load") }}
          </button>
        </div>
      </div>

      <ErrorAlert v-if="errorMessage" :message="errorMessage" />

      <div v-if="history.items.length" class="grid gap-3 md:grid-cols-2">
        <RouterLink
          v-for="item in history.items"
          :key="item.id"
          :to="{ name: 'history-detail', params: { id: item.id } }"
        >
          <HistoryItemCard :item="item" />
        </RouterLink>
      </div>
      <p v-else class="text-sm text-gray-600">{{ t("historyList.empty") }}</p>

      <div class="flex items-center gap-3">
        <button
          class="rounded-md border border-gray-300 px-3 py-1 text-sm font-semibold text-gray-700 hover:bg-gray-100 disabled:cursor-not-allowed disabled:opacity-60"
          :disabled="history.page <= 0 || loading"
          @click="loadHistory(history.page - 1)"
        >
          {{ t("historyList.prev") }}
        </button>
        <span class="text-sm text-gray-700">
          {{ t("historyList.pageLabel", { page: history.page + 1, total: totalPages }) }}
        </span>
        <button
          class="rounded-md border border-gray-300 px-3 py-1 text-sm font-semibold text-gray-700 hover:bg-gray-100 disabled:cursor-not-allowed disabled:opacity-60"
          :disabled="history.page + 1 >= totalPages || loading"
          @click="loadHistory(history.page + 1)"
        >
          {{ t("historyList.next") }}
        </button>
      </div>
    </div>
  </AppLayout>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from "vue";
import { useI18n } from "vue-i18n";
import AppLayout from "../components/AppLayout.vue";
import HistoryItemCard from "../components/HistoryItemCard.vue";
import LoadingSpinner from "../components/LoadingSpinner.vue";
import ErrorAlert from "../components/ErrorAlert.vue";
import { getHistoryPage, type HistoryPage } from "../api/historyApi";
import { HISTORY_PAGE_SIZE } from "../config/appConfig";

const { t } = useI18n();
const userId = ref("");
const loading = ref(false);
const errorMessage = ref("");
const history = reactive<HistoryPage>({ items: [], page: 0, size: HISTORY_PAGE_SIZE, total: 0 });

const totalPages = computed(() => Math.max(1, Math.ceil(history.total / history.size)));

const loadHistory = async (page: number) => {
  if (!userId.value) {
    errorMessage.value = t("historyList.userIdRequired");
    return;
  }
  loading.value = true;
  errorMessage.value = "";
  try {
    const data = await getHistoryPage({ userId: userId.value, page, size: HISTORY_PAGE_SIZE });
    history.items = data.items;
    history.page = data.page;
    history.size = data.size;
    history.total = data.total;
  } catch (err: any) {
    errorMessage.value = err?.message || "Failed to load history";
  } finally {
    loading.value = false;
  }
};
</script>
