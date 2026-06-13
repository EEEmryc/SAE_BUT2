import { create } from "zustand";
import { tokenManager } from "../api/tokenManager";
import { authApi } from "../features/auth/api/authApi";

type AuthState = {
  accessToken: string | null;
  isAuthenticated: boolean;
  isRestoring: boolean;
  setSession: (accessToken: string, refreshToken: string) => void;
  clearSession: () => void;
  restoreSession: () => Promise<void>;
};

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: tokenManager.getAccessToken(),
  isAuthenticated: Boolean(tokenManager.getAccessToken()),
  isRestoring: Boolean(tokenManager.getRefreshToken()),
  setSession: (accessToken, refreshToken) => {
    tokenManager.setAccessToken(accessToken);
    tokenManager.setRefreshToken(refreshToken);
    set({ accessToken, isAuthenticated: true, isRestoring: false });
  },
  clearSession: () => {
    tokenManager.clear();
    set({
      accessToken: null,
      isAuthenticated: false,
      isRestoring: false,
    });
  },
  restoreSession: async () => {
    const refreshToken = tokenManager.getRefreshToken();
    if (!refreshToken) {
      set({ isRestoring: false });
      return;
    }

    try {
      const { token } = await authApi.refresh(refreshToken);
      tokenManager.setAccessToken(token);
      set({
        accessToken: token,
        isAuthenticated: true,
        isRestoring: false,
      });
    } catch {
      tokenManager.clear();
      set({
        accessToken: null,
        isAuthenticated: false,
        isRestoring: false,
      });
    }
  },
}));
