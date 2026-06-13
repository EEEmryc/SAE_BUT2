import { httpClient } from "../../../api/httpClient";

export type LoginPayload = {
  email: string;
  password: string;
};

export type AuthResponse = {
  token: string;
  refreshToken: string;
};

export type ForgotPasswordResponse = {
  message: string;
  token?: string;
};

export type RefreshResponse = {
  token: string;
};

export const authApi = {
  async login(payload: LoginPayload) {
    const response = await httpClient.post<AuthResponse>(
      "/api/auth/login",
      payload,
    );
    return response.data;
  },

  async forgotPassword(email: string) {
    const response = await httpClient.post<ForgotPasswordResponse>(
      "/api/auth/forgot-password",
      { email },
    );
    return response.data;
  },

  async refresh(refreshToken: string) {
    const response = await httpClient.post<RefreshResponse>(
      "/api/auth/refresh",
      undefined,
      {
        headers: {
          "X-Refresh-Token": refreshToken,
        },
      },
    );
    return response.data;
  },
};
