import { createTheme, type PaletteMode } from "@mui/material/styles";
import {
  LH_ERROR,
  LH_ERROR_LIGHT,
  LH_PRIMARY,
  LH_PRIMARY_DARK,
  LH_PRIMARY_LIGHT,
  LH_SUCCESS,
  LH_SUCCESS_LIGHT,
  LH_TEXT_SECONDARY,
  LH_WARNING,
  LH_WARNING_LIGHT,
} from "../styles/tokens";

export function createLearnHubTheme(mode: PaletteMode = "light") {
  const isDark = mode === "dark";

  return createTheme({
    palette: {
      mode,
      primary: {
        main: LH_PRIMARY,
        dark: LH_PRIMARY_DARK,
        light: LH_PRIMARY_LIGHT,
      },
      success: {
        main: LH_SUCCESS,
        light: LH_SUCCESS_LIGHT,
        dark: "#0f5c38",
        contrastText: "#fff",
      },
      warning: {
        main: LH_WARNING,
        light: LH_WARNING_LIGHT,
        dark: "#7a4208",
        contrastText: "#fff",
      },
      error: {
        main: LH_ERROR,
        light: LH_ERROR_LIGHT,
        dark: "#b23c48",
        contrastText: "#fff",
      },
      background: {
        default: isDark ? "#0a1120" : "#f6f8ff",
        paper: isDark ? "#111827" : "#ffffff",
      },
      text: {
        primary: isDark ? "#f6f8ff" : "#17203b",
        secondary: isDark ? "#b8c2d7" : LH_TEXT_SECONDARY,
      },
      divider: isDark ? "#26344d" : "#e3e7f3",
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
      MuiCssBaseline: {
        styleOverrides: {
          body: {
            backgroundColor: isDark ? "#0a1120" : "#f6f8ff",
            color: isDark ? "#f6f8ff" : "#17203b",
          },
        },
      },
      MuiButton: {
        styleOverrides: {
          root: {
            minHeight: 54,
            borderRadius: 10,
            boxShadow: "none",
            "&:hover": { boxShadow: "none" },
          },
        },
      },
      MuiTextField: {
        defaultProps: {
          fullWidth: true,
        },
      },
      MuiPaper: {
        styleOverrides: {
          root: {
            backgroundImage: "none",
            backgroundColor: isDark ? "#111827" : "#ffffff",
          },
        },
      },
    },
  });
}

export const muiTheme = createLearnHubTheme("light");
