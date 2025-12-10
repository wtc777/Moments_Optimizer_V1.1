<template>
  <div class="rounded-lg border border-gray-200 bg-white p-4">
    <h3 class="mb-3 text-sm font-semibold text-gray-800">{{ t("taskSteps.title") }}</h3>
    <ul class="space-y-3">
      <li v-for="step in steps" :key="step.key" class="flex items-start gap-3">
        <div
          class="mt-0.5 h-3 w-3 rounded-full"
          :class="statusColor(step.status)"
          aria-hidden="true"
        ></div>
        <div class="flex-1">
          <p class="text-sm font-medium text-gray-900">{{ step.label }}</p>
          <p class="text-xs text-gray-600">
            {{ t("taskSteps.status") }}: {{ step.status }}
            <span v-if="step.startedAt" class="ml-2">
              {{ t("taskSteps.startedAt") }}: {{ formatDate(step.startedAt) }}
            </span>
            <span v-if="step.finishedAt" class="ml-2">
              {{ t("taskSteps.finishedAt") }}: {{ formatDate(step.finishedAt) }}
            </span>
          </p>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n";
import type { TaskStep } from "../api/taskApi";

defineProps<{
  steps: TaskStep[];
}>();

const { t } = useI18n();

const statusColor = (status: string) => {
  switch (status) {
    case "SUCCESS":
      return "bg-green-500";
    case "RUNNING":
      return "bg-blue-500";
    case "FAILED":
      return "bg-red-500";
    default:
      return "bg-gray-400";
  }
};

const formatDate = (value?: string | null) => {
  if (!value) return "";
  return new Date(value).toLocaleString();
};
</script>
