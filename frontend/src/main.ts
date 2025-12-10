import { createApp } from "vue";
import { createI18n } from "vue-i18n";
import App from "./App.vue";
import router from "./router";
import enCommon from "./locales/en/common.json";
import enViews from "./locales/en/views.json";
import zhCommon from "./locales/zh/common.json";
import zhViews from "./locales/zh/views.json";
import "./styles/tailwind.css";

const i18n = createI18n({
  legacy: false,
  locale: "en",
  fallbackLocale: "en",
  messages: {
    en: { ...enCommon, ...enViews },
    zh: { ...zhCommon, ...zhViews }
  }
});

createApp(App).use(router).use(i18n).mount("#app");
