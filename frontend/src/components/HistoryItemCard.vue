<template>
  <div class="rounded-lg border border-gray-200 bg-white p-4 hover:border-indigo-200 hover:shadow-sm">
    <p class="text-sm font-semibold text-gray-800">{{ formatDate(item.createdAt) }}</p>
    <p class="mt-1 text-sm text-gray-700 truncate">{{ item.inputText || t("historyItem.noInput") }}</p>
    <p class="mt-2 text-xs font-medium" :class="item.success ? 'text-green-600' : 'text-red-600'">
      {{ item.success ? t("historyItem.success") : t("historyItem.failed") }}
    </p>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n";
import type { HistorySummary } from "../api/historyApi";

defineProps<{ item: HistorySummary }>();
const { t } = useI18n();

const formatDate = (value: string) => new Date(value).toLocaleString();
</script>
