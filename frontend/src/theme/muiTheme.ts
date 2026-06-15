import { createTheme, type Theme } from "@mui/material/styles";
import type { PaletteMode } from "@mui/material";

export function createAppTheme(mode: PaletteMode): Theme {
  const isLight = mode === "light";

  return createTheme({
    palette: {
      mode,
      primary: {
        main: "#4f5ff7",
        dark: "#3444dc",
        light: "#7f75ff",
      },
      background: isLight
        ? { default: "#f6f8ff", paper: "#ffffff" }
        : { default: "#0f1320", paper: "#171c2e" },
      text: isLight
        ? { primary: "#17203b", secondary: "#596783" }
        : { primary: "#e7ebf6", secondary: "#9aa6c2" },
      error: {
        main: "#d14343",
      },
    },
    typography: {
      fontFamily:
        '"Inter", "Segoe UI", Roboto, Helvetica, Arial, sans-serif',
      h1: {
        fontWeight: 800,
        letterSpacing: "-0.04em",
      },
      h2: {
        fontWeight: 800,
        letterSpacing: "-0.035em",
      },
      button: {
        fontWeight: 700,
        textTransform: "none",
      },
    },
    shape: {
      borderRadius: 14,
    },
    components: {
      MuiButton: {
        styleOverrides: {
          root: {
            minHeight: 54,
            borderRadius: 10,
            boxShadow: "none",
          },
        },
      },
      MuiTextField: {
        defaultProps: {
          fullWidth: true,
        },
      },
    },
  });
}

export const muiTheme = createAppTheme("light");