import { create } from "zustand";

export type ThemeMode = "light" | "dark";

const STORAGE_KEY = "learnhub-theme-mode";

function getStoredThemeMode(): ThemeMode {
  if (typeof window === "undefined") {
    return "light";
  }

  return window.localStorage.getItem(STORAGE_KEY) === "dark"
    ? "dark"
    : "light";
}

function saveThemeMode(mode: ThemeMode) {
  if (typeof window !== "undefined") {
    window.localStorage.setItem(STORAGE_KEY, mode);
  }
}

type ThemeState = {
  mode: ThemeMode;
  isDarkMode: boolean;
  setMode: (mode: ThemeMode) => void;
  toggleMode: () => void;
};

export const useThemeStore = create<ThemeState>((set, get) => {
  const initialMode = getStoredThemeMode();

  return {
    mode: initialMode,
    isDarkMode: initialMode === "dark",
    setMode: (mode) => {
      saveThemeMode(mode);
      set({ mode, isDarkMode: mode === "dark" });
    },
    toggleMode: () => {
      get().setMode(get().mode === "dark" ? "light" : "dark");
    },
  };
});
