<template>
  <AppLayout>
    <div class="space-y-4">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-xl font-semibold text-gray-900">{{ t("historyDetail.title") }}</h1>
          <p class="text-sm text-gray-600">{{ t("historyDetail.recordId") }}: {{ historyId }}</p>
        </div>
        <LoadingSpinner v-if="loading" :message="t('historyDetail.loading')" />
      </div>

      <ErrorAlert v-if="errorMessage" :message="errorMessage" />

      <div v-if="record" class="rounded-lg border border-gray-200 bg-white p-4 shadow-sm space-y-3">
        <div>
          <h3 class="text-sm font-semibold text-gray-800">{{ t("historyDetail.inputText") }}</h3>
          <p class="text-sm text-gray-700 whitespace-pre-wrap">{{ record.inputText || "-" }}</p>
        </div>
        <div>
          <h3 class="text-sm font-semibold text-gray-800">{{ t("historyDetail.outputText") }}</h3>
          <p class="text-sm text-gray-700 whitespace-pre-wrap">{{ record.outputText || "-" }}</p>
        </div>
        <div class="grid gap-3 md:grid-cols-2">
          <div class="rounded-md bg-gray-50 p-3 text-sm text-gray-700">
            <p><span class="font-semibold">{{ t("historyDetail.model") }}:</span> {{ record.modelName || "-" }}</p>
            <p><span class="font-semibold">{{ t("historyDetail.duration") }}:</span> {{ record.durationMs ?? "-" }} ms</p>
            <p>
              <span class="font-semibold">{{ t("historyDetail.tokens") }}:</span>
              {{ record.totalTokens ?? "-" }} ({{ record.inputTokens ?? "-" }}/{{ record.outputTokens ?? "-" }})
            </p>
          </div>
          <div class="rounded-md bg-gray-50 p-3 text-sm text-gray-700">
            <p>
              <span class="font-semibold">{{ t("historyDetail.status") }}:</span>
              <span :class="record.success ? 'text-green-600' : 'text-red-600'">
                {{ record.success ? t("historyDetail.success") : t("historyDetail.failed") }}
              </span>
            </p>
            <p v-if="record.errorMessage" class="mt-1 text-red-600">{{ record.errorMessage }}</p>
          </div>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import { useI18n } from "vue-i18n";
import AppLayout from "../components/AppLayout.vue";
import LoadingSpinner from "../components/LoadingSpinner.vue";
import ErrorAlert from "../components/ErrorAlert.vue";
import { getHistoryById, type HistoryDetail } from "../api/historyApi";

const route = useRoute();
const { t } = useI18n();
const historyId = route.params.id as string;

const record = ref<HistoryDetail | null>(null);
const loading = ref(false);
const errorMessage = ref("");

const loadRecord = async () => {
  loading.value = true;
  errorMessage.value = "";
  try {
    record.value = await getHistoryById(historyId);
  } catch (err: any) {
    errorMessage.value = err?.message || "Failed to load history";
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadRecord();
});
</script>
