import httpClient from "./httpClient";

export interface TaskStep {
  key: string;
  label: string;
  status: string;
  startedAt?: string | null;
  finishedAt?: string | null;
}

export interface TaskDetail {
  id: string;
  type: string;
  status: string;
  createdAt: string;
  updatedAt: string;
  steps: TaskStep[];
  resultJson?: Record<string, unknown> | null;
  errorMessage?: string | null;
}

export interface CreateTaskRequest {
  userId: string;
  type?: string;
  inputText?: string | null;
  imageUrls?: string[];
  options?: Record<string, unknown>;
}

export async function createTask(payload: CreateTaskRequest): Promise<TaskDetail> {
  return httpClient.post("/api/tasks", payload);
}

export async function getTaskById(taskId: string): Promise<TaskDetail> {
  return httpClient.get(`/api/tasks/${taskId}`);
}
