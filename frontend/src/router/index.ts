import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import HomeView from "../views/HomeView.vue";
import TaskDetailView from "../views/TaskDetailView.vue";
import HistoryListView from "../views/HistoryListView.vue";
import HistoryDetailView from "../views/HistoryDetailView.vue";
import LoginView from "../views/LoginView.vue";

const routes: RouteRecordRaw[] = [
  { path: "/", name: "home", component: HomeView },
  { path: "/login", name: "login", component: LoginView },
  { path: "/tasks/:id", name: "task-detail", component: TaskDetailView, props: true },
  { path: "/history", name: "history-list", component: HistoryListView },
  { path: "/history/:id", name: "history-detail", component: HistoryDetailView, props: true }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
