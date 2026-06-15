import { create } from "zustand";

export type ThemeMode = "light" | "dark";

const STORAGE_KEY = "learnhub-theme-mode";

function getInitialMode(): ThemeMode {
  const stored = localStorage.getItem(STORAGE_KEY);
  return stored === "dark" ? "dark" : "light";
}

type ThemeState = {
  mode: ThemeMode;
  toggleMode: () => void;
  setMode: (mode: ThemeMode) => void;
};

export const useThemeStore = create<ThemeState>((set) => ({
  mode: getInitialMode(),
  toggleMode: () =>
    set((state) => {
      const next: ThemeMode = state.mode === "light" ? "dark" : "light";
      localStorage.setItem(STORAGE_KEY, next);
      return { mode: next };
    }),
  setMode: (mode) => {
    localStorage.setItem(STORAGE_KEY, mode);
    set({ mode });
  },
}));