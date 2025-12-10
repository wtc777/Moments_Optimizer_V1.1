<template>
  <AppLayout>
    <div class="space-y-6">
      <header class="space-y-2">
        <h1 class="text-2xl font-semibold text-gray-900">{{ t("home.title") }}</h1>
        <p class="text-sm text-gray-600">{{ t("home.subtitle") }}</p>
      </header>

      <section class="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
        <h2 class="mb-4 text-lg font-semibold text-gray-900">{{ t("home.createSection") }}</h2>
        <TaskForm @created="handleCreated" />
      </section>

      <RouterLink
        to="/history"
        class="inline-flex items-center text-sm font-medium text-indigo-600 hover:text-indigo-700"
      >
        {{ t("home.viewHistory") }}
      </RouterLink>
    </div>
  </AppLayout>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import TaskForm from "../components/TaskForm.vue";
import AppLayout from "../components/AppLayout.vue";
import type { TaskDetail } from "../api/taskApi";

const router = useRouter();
const { t } = useI18n();

const handleCreated = (task: TaskDetail) => {
  router.push({ name: "task-detail", params: { id: task.id } });
};
</script>
