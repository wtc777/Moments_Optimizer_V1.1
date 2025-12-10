<template>
  <form class="space-y-4" @submit.prevent="handleSubmit">
    <div>
      <label class="block text-sm font-medium text-gray-700">{{ t("taskForm.userId") }}</label>
      <input
        v-model="form.userId"
        type="text"
        required
        class="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-indigo-500 focus:outline-none"
        :placeholder="t('taskForm.userIdPlaceholder')"
      />
    </div>
    <div>
      <label class="block text-sm font-medium text-gray-700">{{ t("taskForm.type") }}</label>
      <input
        v-model="form.type"
        type="text"
        class="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-indigo-500 focus:outline-none"
      />
    </div>
    <div>
      <label class="block text-sm font-medium text-gray-700">{{ t("taskForm.inputText") }}</label>
      <textarea
        v-model="form.inputText"
        rows="4"
        class="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-indigo-500 focus:outline-none"
        :placeholder="t('taskForm.inputTextPlaceholder')"
      ></textarea>
    </div>
    <div>
      <label class="block text-sm font-medium text-gray-700">{{ t("taskForm.imageUrls") }}</label>
      <textarea
        v-model="imageUrlsInput"
        rows="2"
        class="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-indigo-500 focus:outline-none"
        :placeholder="t('taskForm.imageUrlsPlaceholder')"
      ></textarea>
      <p class="mt-1 text-xs text-gray-500">{{ t("taskForm.imageUrlsHint") }}</p>
    </div>
    <div class="flex items-center gap-3">
      <button
        type="submit"
        :disabled="loading"
        class="rounded-md bg-indigo-600 px-4 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-700 disabled:cursor-not-allowed disabled:bg-indigo-400"
      >
        {{ loading ? t("taskForm.creating") : t("taskForm.createTask") }}
      </button>
      <LoadingSpinner v-if="loading" :message="t('taskForm.loadingLabel')" />
    </div>
    <ErrorAlert v-if="errorMessage" :message="errorMessage" />
  </form>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useI18n } from "vue-i18n";
import LoadingSpinner from "./LoadingSpinner.vue";
import ErrorAlert from "./ErrorAlert.vue";
import { createTask, type CreateTaskRequest, type TaskDetail } from "../api/taskApi";

const emit = defineEmits<{ (e: "created", task: TaskDetail): void }>();
const { t } = useI18n();

const form = ref<CreateTaskRequest>({
  userId: "",
  type: "moments_optimize",
  inputText: "",
  imageUrls: []
});

const imageUrlsInput = ref("");
const loading = ref(false);
const errorMessage = ref("");

const handleSubmit = async () => {
  loading.value = true;
  errorMessage.value = "";
  try {
    const imageList = imageUrlsInput.value
      .split(",")
      .map((v) => v.trim())
      .filter((v) => v.length > 0);
    const payload: CreateTaskRequest = {
      ...form.value,
      imageUrls: imageList
    };
    const task = await createTask(payload);
    emit("created", task);
  } catch (err: any) {
    errorMessage.value = err?.message || "Failed to create task";
  } finally {
    loading.value = false;
  }
};
</script>
