<template>
  <div class="min-h-screen bg-gray-50 text-gray-900">
    <header class="bg-white shadow-sm">
      <div class="mx-auto flex max-w-5xl items-center justify-between px-4 py-3">
        <RouterLink to="/" class="text-lg font-semibold text-indigo-600">Moments Optimizer</RouterLink>
        <nav class="flex items-center gap-4 text-sm font-medium text-gray-700">
          <RouterLink class="hover:text-indigo-600" to="/">{{ t("nav.home") }}</RouterLink>
          <RouterLink class="hover:text-indigo-600" to="/history">{{ t("nav.history") }}</RouterLink>
        </nav>
      </div>
    </header>
    <main class="mx-auto max-w-5xl px-4 py-6">
      <slot />
    </main>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n";

const { t } = useI18n();
</script>
