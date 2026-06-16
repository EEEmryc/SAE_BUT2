import { beforeEach, describe, expect, it, vi } from "vitest";

async function importFreshStore() {
  vi.resetModules();
  return import("./themeStore");
}

describe("themeStore", () => {
  beforeEach(() => {
    window.localStorage.clear();
  });

  it("utilise le mode clair par défaut", async () => {
    const { useThemeStore } = await importFreshStore();

    expect(useThemeStore.getState().mode).toBe("light");
    expect(useThemeStore.getState().isDarkMode).toBe(false);
  });

  it("restaure et sauvegarde le mode sombre", async () => {
    window.localStorage.setItem("learnhub-theme-mode", "dark");
    const { useThemeStore } = await importFreshStore();

    expect(useThemeStore.getState().mode).toBe("dark");

    useThemeStore.getState().toggleMode();

    expect(useThemeStore.getState().mode).toBe("light");
    expect(window.localStorage.getItem("learnhub-theme-mode")).toBe("light");
  });
});
