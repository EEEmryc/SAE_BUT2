import { createTheme } from "@mui/material/styles";
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

export const muiTheme = createTheme({
  palette: {
    mode: "light",
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
      default: "#f6f8ff",
      paper: "#ffffff",
    },
    text: {
      primary: "#17203b",
      secondary: LH_TEXT_SECONDARY,
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
        },
      },
    },
  },
});
