import httpClient from "./httpClient";

export interface LoginRequest {
  phone: string;
  password: string;
}

export interface RegisterRequest {
  phone: string;
  nickname: string;
  password: string;
  password_confirm: string;
}

export async function login(payload: LoginRequest): Promise<any> {
  return httpClient.post("/auth/login", payload);
}

export async function register(payload: RegisterRequest): Promise<any> {
  return httpClient.post("/auth/register", payload);
}
