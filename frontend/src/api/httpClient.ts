import axios from "axios";
import { env } from "../app/config/env";
import { tokenManager } from "./tokenManager";

export const httpClient = axios.create({
  baseURL: env.apiUrl,
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 10_000,
});

httpClient.interceptors.request.use((config) => {
  const token = tokenManager.getAccessToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});
