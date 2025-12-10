import httpClient from "./httpClient";

export interface HistorySummary {
  id: string;
  userId: string;
  createdAt: string;
  imagePath?: string | null;
  inputText?: string | null;
  outputText?: string | null;
  durationMs?: number | null;
  modelName?: string | null;
  success?: boolean;
  errorMessage?: string | null;
}

export interface HistoryPage {
  items: HistorySummary[];
  page: number;
  size: number;
  total: number;
}

export interface HistoryDetail extends HistorySummary {
  inputTokens?: number | null;
  outputTokens?: number | null;
  totalTokens?: number | null;
}

export async function getHistoryPage(params: {
  userId: string;
  page?: number;
  size?: number;
}): Promise<HistoryPage> {
  const { userId, page = 0, size = 20 } = params;
  return httpClient.get("/api/history", { params: { userId, page, size } });
}

export async function getHistoryById(id: string): Promise<HistoryDetail> {
  return httpClient.get(`/api/history/${id}`);
}
