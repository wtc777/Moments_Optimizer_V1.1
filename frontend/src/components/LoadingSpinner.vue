<template>
  <div class="flex items-center gap-2 text-sm text-gray-600">
    <span class="inline-block h-4 w-4 animate-spin rounded-full border-2 border-indigo-500 border-t-transparent"></span>
    <span>{{ message }}</span>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{ message?: string }>();
const message = props.message ?? "Loading...";
</script>
