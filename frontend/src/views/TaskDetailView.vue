<template>
  <AppLayout>
    <div class="space-y-4">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-xl font-semibold text-gray-900">{{ t("taskDetail.title") }}</h1>
          <p class="text-sm text-gray-600">
            {{ t("taskDetail.taskId") }}: {{ taskId }} ·
            {{ t("taskDetail.status") }}: <span class="font-semibold">{{ task?.status }}</span>
          </p>
        </div>
        <LoadingSpinner v-if="loading" :message="t('taskDetail.loading')" />
      </div>

      <ErrorAlert v-if="errorMessage" :message="errorMessage" />

      <div v-if="task" class="space-y-4">
        <TaskStepsTimeline :steps="task.steps" />

        <div v-if="task.status === 'SUCCESS'">
          <TaskResultCard :result="task.resultJson || {}" />
        </div>
        <div v-else-if="task.status === 'FAILED' && task.errorMessage">
          <ErrorAlert :message="task.errorMessage" />
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import { useI18n } from "vue-i18n";
import AppLayout from "../components/AppLayout.vue";
import TaskStepsTimeline from "../components/TaskStepsTimeline.vue";
import TaskResultCard from "../components/TaskResultCard.vue";
import LoadingSpinner from "../components/LoadingSpinner.vue";
import ErrorAlert from "../components/ErrorAlert.vue";
import { getTaskById, type TaskDetail } from "../api/taskApi";
import { POLLING_INTERVAL_MS } from "../config/appConfig";

const route = useRoute();
const { t } = useI18n();
const taskId = route.params.id as string;

const task = ref<TaskDetail | null>(null);
const loading = ref(false);
const errorMessage = ref("");
let timer: number | null = null;

const fetchTask = async () => {
  loading.value = true;
  errorMessage.value = "";
  try {
    task.value = await getTaskById(taskId);
  } catch (err: any) {
    errorMessage.value = err?.message || "Failed to load task";
  } finally {
    loading.value = false;
  }
};

const startPolling = () => {
  stopPolling();
  timer = window.setInterval(async () => {
    await fetchTask();
    if (task.value && task.value.status !== "PENDING" && task.value.status !== "RUNNING") {
      stopPolling();
    }
  }, POLLING_INTERVAL_MS);
};

const stopPolling = () => {
  if (timer) {
    window.clearInterval(timer);
    timer = null;
  }
};

onMounted(async () => {
  await fetchTask();
  if (task.value && (task.value.status === "PENDING" || task.value.status === "RUNNING")) {
    startPolling();
  }
});

onBeforeUnmount(() => {
  stopPolling();
});
</script>
