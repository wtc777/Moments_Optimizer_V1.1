<template>
  <div class="rounded-lg border border-gray-200 bg-white p-4">
    <h3 class="mb-2 text-sm font-semibold text-gray-800">{{ t("taskResult.title") }}</h3>
    <pre class="overflow-auto rounded-md bg-gray-50 p-3 text-xs text-gray-800">{{ formatted }}</pre>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useI18n } from "vue-i18n";

const props = defineProps<{ result: Record<string, unknown> | null | undefined }>();
const { t } = useI18n();

const formatted = computed(() => JSON.stringify(props.result ?? {}, null, 2));
</script>
