import axios, { type InternalAxiosRequestConfig } from "axios";
import { env } from "../app/config/env";
import { tokenManager } from "./tokenManager";
import { httpClient } from "./httpClient";

type RetryableRequest = InternalAxiosRequestConfig & { _retry?: boolean };

let refreshPromise: Promise<string> | null = null;

async function refreshAccessToken() {
  const refreshToken = tokenManager.getRefreshToken();
  if (!refreshToken) {
    throw new Error("Aucun refresh token disponible.");
  }

  const response = await axios.post<{ token: string }>(
    `${env.apiUrl}/api/auth/refresh`,
    undefined,
    {
      headers: {
        "X-Refresh-Token": refreshToken,
      },
    },
  );

  tokenManager.setAccessToken(response.data.token);
  return response.data.token;
}

httpClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const request = error.config as RetryableRequest | undefined;
    const isAuthenticationCall = request?.url?.startsWith("/api/auth/");

    if (
      error.response?.status !== 401 ||
      !request ||
      request._retry ||
      isAuthenticationCall
    ) {
      return Promise.reject(error);
    }

    request._retry = true;

    try {
      refreshPromise ??= refreshAccessToken().finally(() => {
        refreshPromise = null;
      });
      const token = await refreshPromise;
      request.headers.Authorization = `Bearer ${token}`;
      return httpClient(request);
    } catch (refreshError) {
      tokenManager.clear();
      window.dispatchEvent(new Event("learnhub:session-expired"));
      return Promise.reject(refreshError);
    }
  },
);
