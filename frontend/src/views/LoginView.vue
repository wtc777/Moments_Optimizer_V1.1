<template>
  <div class="relative min-h-screen overflow-hidden bg-[#F7F9FC] text-slate-900">
    <div class="pointer-events-none absolute inset-0">
      <div class="absolute -left-16 -top-10 h-80 w-80 bg-gradient-to-br from-blue-200/50 to-blue-100/40 blur-3xl"></div>
      <div class="absolute -right-10 top-10 h-96 w-96 bg-gradient-to-br from-cyan-200/50 to-blue-50/50 blur-3xl"></div>
    </div>

    <header class="relative flex items-center justify-between px-6 py-5">
      <RouterLink to="/" class="text-lg font-semibold text-indigo-600">Moments Optimizer</RouterLink>
      <nav class="flex items-center gap-4 text-sm font-medium text-slate-700">
        <RouterLink class="hover:text-indigo-600" to="/">{{ t("nav.home") }}</RouterLink>
        <RouterLink class="hover:text-indigo-600" to="/history">{{ t("nav.history") }}</RouterLink>
        <RouterLink class="text-indigo-600" to="/login">{{ t("login.navLogin") }}</RouterLink>
      </nav>
    </header>

    <main class="relative mx-auto flex max-w-4xl flex-col gap-6 px-4 pb-10">
      <header class="space-y-2">
        <p class="text-sm font-semibold uppercase tracking-wide text-blue-500">{{ t("login.tagline") }}</p>
        <h1 class="text-3xl font-bold text-slate-900">{{ t("login.title") }}</h1>
        <p class="text-slate-500">{{ t("login.subtitle") }}</p>
      </header>

      <section class="glass rounded-3xl border border-white/60 bg-white/70 p-6 shadow-sm backdrop-blur-xl">
        <div class="flex flex-col items-start justify-between gap-4 md:flex-row md:items-center">
          <p class="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">{{ t("login.accountLabel") }}</p>
          <div class="inline-flex items-center gap-2 rounded-full border border-slate-100 bg-slate-50 p-1">
            <button
              class="auth-tab rounded-full px-4 py-2 text-sm font-semibold shadow-sm"
              :class="mode === 'login' ? 'bg-white text-blue-600 border border-blue-100' : 'text-slate-500'"
              @click="switchMode('login')"
            >
              {{ t("login.tabLogin") }}
            </button>
            <button
              class="auth-tab rounded-full px-4 py-2 text-sm font-semibold"
              :class="mode === 'register' ? 'bg-white text-blue-600 shadow-sm border border-blue-100' : 'text-slate-500'"
              @click="switchMode('register')"
            >
              {{ t("login.tabRegister") }}
            </button>
          </div>
        </div>

        <div class="mt-6 grid gap-6 md:grid-cols-2">
          <form v-if="mode === 'login'" class="space-y-4 md:col-span-2" @submit.prevent="handleLogin">
            <div>
              <label class="mb-1 block text-sm font-medium text-slate-700">{{ t("login.phoneLabel") }}</label>
              <input
                v-model="loginForm.phone"
                type="tel"
                inputmode="numeric"
                maxlength="11"
                class="w-full rounded-2xl border border-slate-200 bg-white/80 px-4 py-3 text-sm text-slate-700 shadow-sm focus:border-indigo-300 focus:ring-2 focus:ring-indigo-200"
                :placeholder="t('login.phonePlaceholder')"
                required
              />
            </div>
            <div>
              <label class="mb-1 block text-sm font-medium text-slate-700">{{ t("login.passwordLabel") }}</label>
              <input
                v-model="loginForm.password"
                type="password"
                class="w-full rounded-2xl border border-slate-200 bg-white/80 px-4 py-3 text-sm text-slate-700 shadow-sm focus:border-indigo-300 focus:ring-2 focus:ring-indigo-200"
                :placeholder="t('login.passwordPlaceholder')"
                required
              />
            </div>
            <div class="flex flex-col gap-3 sm:flex-row sm:items-center">
              <button
                type="submit"
                :disabled="loading"
                class="relative w-full overflow-hidden rounded-2xl py-3 text-sm font-semibold text-white shadow-lg shadow-blue-500/30 transition hover:shadow-blue-500/40 disabled:cursor-not-allowed sm:w-auto sm:px-6"
              >
                <div class="absolute inset-0 bg-gradient-to-r from-[#4F7BFF] to-[#6DD5FA]"></div>
                <span class="relative flex items-center justify-center gap-2">
                  <span v-if="loading" class="h-4 w-4 animate-spin rounded-full border-2 border-blue-100 border-t-transparent"></span>
                  <span>{{ loading ? t("login.signingIn") : t("login.submitLogin") }}</span>
                </span>
              </button>
              <ErrorAlert v-if="errorMessage" :message="errorMessage" />
              <p v-else class="text-sm text-slate-500">{{ t("login.tipsLogin") }}</p>
            </div>
          </form>

          <form v-else class="space-y-4 md:col-span-2" @submit.prevent="handleRegister">
            <div class="grid gap-4 md:grid-cols-2">
              <div>
                <label class="mb-1 block text-sm font-medium text-slate-700">{{ t("login.phoneLabel") }}</label>
                <input
                  v-model="registerForm.phone"
                  type="tel"
                  inputmode="numeric"
                  maxlength="11"
                  class="w-full rounded-2xl border border-slate-200 bg-white/80 px-4 py-3 text-sm text-slate-700 shadow-sm focus:border-indigo-300 focus:ring-2 focus:ring-indigo-200"
                  :placeholder="t('login.phonePlaceholder')"
                  required
                />
              </div>
              <div>
                <label class="mb-1 block text-sm font-medium text-slate-700">{{ t("login.nicknameLabel") }}</label>
                <input
                  v-model="registerForm.nickname"
                  type="text"
                  class="w-full rounded-2xl border border-slate-200 bg-white/80 px-4 py-3 text-sm text-slate-700 shadow-sm focus:border-indigo-300 focus:ring-2 focus:ring-indigo-200"
                  :placeholder="t('login.nicknamePlaceholder')"
                  required
                />
              </div>
            </div>
            <p class="text-sm text-slate-500">{{ t("login.accountTip") }}</p>
            <div class="grid gap-4 md:grid-cols-2">
              <div>
                <label class="mb-1 block text-sm font-medium text-slate-700">{{ t("login.passwordLabel") }}</label>
                <input
                  v-model="registerForm.password"
                  type="password"
                  class="w-full rounded-2xl border border-slate-200 bg-white/80 px-4 py-3 text-sm text-slate-700 shadow-sm focus:border-indigo-300 focus:ring-2 focus:ring-indigo-200"
                  :placeholder="t('login.passwordRegisterPlaceholder')"
                  required
                />
              </div>
              <div>
                <label class="mb-1 block text-sm font-medium text-slate-700">{{ t("login.passwordConfirmLabel") }}</label>
                <input
                  v-model="registerForm.password_confirm"
                  type="password"
                  class="w-full rounded-2xl border border-slate-200 bg-white/80 px-4 py-3 text-sm text-slate-700 shadow-sm focus:border-indigo-300 focus:ring-2 focus:ring-indigo-200"
                  :placeholder="t('login.passwordConfirmPlaceholder')"
                  required
                />
              </div>
            </div>
            <div class="flex flex-col gap-3 sm:flex-row sm:items-center">
              <button
                type="submit"
                :disabled="loading"
                class="relative w-full overflow-hidden rounded-2xl py-3 text-sm font-semibold text-white shadow-lg shadow-blue-500/30 transition hover:shadow-blue-500/40 disabled:cursor-not-allowed sm:w-auto sm:px-6"
              >
                <div class="absolute inset-0 bg-gradient-to-r from-[#4F7BFF] to-[#6DD5FA]"></div>
                <span class="relative flex items-center justify-center gap-2">
                  <span v-if="loading" class="h-4 w-4 animate-spin rounded-full border-2 border-blue-100 border-t-transparent"></span>
                  <span>{{ loading ? t("login.registering") : t("login.submitRegister") }}</span>
                </span>
              </button>
              <ErrorAlert v-if="errorMessage" :message="errorMessage" />
              <p v-else class="text-sm text-slate-500">{{ t("login.tipsRegister") }}</p>
            </div>
          </form>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { useI18n } from "vue-i18n";
import { login, register } from "../api/authApi";
import ErrorAlert from "../components/ErrorAlert.vue";

const { t } = useI18n();
const mode = ref<"login" | "register">("login");
const loading = ref(false);
const errorMessage = ref("");

const loginForm = reactive({
  phone: "",
  password: ""
});

const registerForm = reactive({
  phone: "",
  nickname: "",
  password: "",
  password_confirm: ""
});

const switchMode = (val: "login" | "register") => {
  mode.value = val;
  errorMessage.value = "";
};

const validatePhone = (phone: string) => /^\d{11}$/.test((phone || "").trim());

const handleLogin = async () => {
  if (!validatePhone(loginForm.phone)) {
    errorMessage.value = t("login.invalidPhone");
    return;
  }
  if (!loginForm.password) {
    errorMessage.value = t("login.needPassword");
    return;
  }
  loading.value = true;
  errorMessage.value = "";
  try {
    await login({ ...loginForm });
    errorMessage.value = t("login.loginSuccess");
  } catch (err: any) {
    errorMessage.value = err?.message || t("login.loginFailed");
  } finally {
    loading.value = false;
  }
};

const handleRegister = async () => {
  if (!validatePhone(registerForm.phone)) {
    errorMessage.value = t("login.invalidPhone");
    return;
  }
  if (!registerForm.nickname) {
    errorMessage.value = t("login.needNickname");
    return;
  }
  if (!registerForm.password || registerForm.password.length < 6) {
    errorMessage.value = t("login.passwordTooShort");
    return;
  }
  if (registerForm.password !== registerForm.password_confirm) {
    errorMessage.value = t("login.passwordConfirmMismatch");
    return;
  }
  loading.value = true;
  errorMessage.value = "";
  try {
    await register({ ...registerForm });
    errorMessage.value = t("login.registerSuccess");
  } catch (err: any) {
    errorMessage.value = err?.message || t("login.registerFailed");
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.glass {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.5);
}
</style>
