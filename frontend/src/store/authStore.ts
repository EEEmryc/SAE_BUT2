import { create } from "zustand";
import { tokenManager } from "../http/tokenManager";
import {
  authApi,
  type UserProfile,
  type UserRole,
} from "../features/auth/services/authApi";

type AuthState = {
  accessToken: string | null;
  user: UserProfile | null;
  isAuthenticated: boolean;
  isRestoring: boolean;
  establishSession: (
    accessToken: string,
    refreshToken: string,
  ) => Promise<void>;
  clearSession: () => void;
  logout: () => Promise<void>;
  restoreSession: () => Promise<void>;
  setUser: (user: Omit<UserProfile, "role"> & { role: string }) => void;
};

function normalizeProfile(
  profile: Omit<UserProfile, "role"> & { role: string },
): UserProfile {
  return {
    ...profile,
    role: profile.role.replace("ROLE_", "") as UserRole,
  };
}

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: tokenManager.getAccessToken(),
  user: null,
  isAuthenticated: Boolean(tokenManager.getAccessToken()),
  isRestoring: Boolean(tokenManager.getRefreshToken()),
  establishSession: async (accessToken, refreshToken) => {
    tokenManager.setAccessToken(accessToken);
    tokenManager.setRefreshToken(refreshToken);

    try {
      const user = normalizeProfile(await authApi.me());
      set({
        accessToken,
        user,
        isAuthenticated: true,
        isRestoring: false,
      });
    } catch (error) {
      tokenManager.clear();
      set({
        accessToken: null,
        user: null,
        isAuthenticated: false,
        isRestoring: false,
      });
      throw error;
    }
  },
  clearSession: () => {
    tokenManager.clear();
    set({
      accessToken: null,
      user: null,
      isAuthenticated: false,
      isRestoring: false,
    });
  },
  logout: async () => {
    const refreshToken = tokenManager.getRefreshToken();

    try {
      if (refreshToken) {
        await authApi.logout(refreshToken);
      }
    } finally {
      tokenManager.clear();
      set({
        accessToken: null,
        user: null,
        isAuthenticated: false,
        isRestoring: false,
      });
    }
  },
  setUser: (user) => {
    set({ user: normalizeProfile(user) });
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
      const user = normalizeProfile(await authApi.me());
      set({
        accessToken: token,
        user,
        isAuthenticated: true,
        isRestoring: false,
      });
    } catch {
      tokenManager.clear();
      set({
        accessToken: null,
        user: null,
        isAuthenticated: false,
        isRestoring: false,
      });
    }
  },
}));
