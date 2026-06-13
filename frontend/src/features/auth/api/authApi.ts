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

export type UserRole = "ETUDIANT" | "PROFESSEUR" | "ADMIN";

export type UserProfile = {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: UserRole;
  statut: string;
};

type ApiUserProfile = Omit<UserProfile, "role"> & {
  role: UserRole | `ROLE_${UserRole}`;
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

  async me() {
    const response = await httpClient.get<ApiUserProfile>("/api/auth/me");
    return response.data;
  },

  async logout(refreshToken: string) {
    await httpClient.post(
      "/api/auth/logout",
      undefined,
      {
        headers: {
          "X-Refresh-Token": refreshToken,
        },
      },
    );
  },
};
