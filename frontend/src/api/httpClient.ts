import axios, { AxiosError } from "axios";

export interface ApiError extends Error {
  code?: string;
  status?: number;
}

const instance = axios.create({
  baseURL: "/",
  headers: {
    "Content-Type": "application/json"
  }
});

instance.interceptors.response.use(
  (response) => {
    const { data } = response;
    if (data && data.success === true) {
      return data.data;
    }
    const error: ApiError = new Error(data?.message || "Request failed");
    error.code = data?.code || "API_ERROR";
    error.status = response.status;
    return Promise.reject(error);
  },
  (error: AxiosError) => {
    const apiError: ApiError = new Error(error.message || "Network error");
    apiError.code = "HTTP_ERROR";
    apiError.status = error.response?.status;
    return Promise.reject(apiError);
  }
);

export default instance;
