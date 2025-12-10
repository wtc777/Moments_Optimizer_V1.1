<template>
  <div class="rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
    <p class="font-semibold">{{ title }}</p>
    <p class="mt-1">{{ message }}</p>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  title?: string;
  message: string;
}>();

const title = props.title ?? "Error";
</script>
