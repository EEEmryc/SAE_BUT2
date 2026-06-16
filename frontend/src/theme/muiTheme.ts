import { createTheme } from "@mui/material/styles";

export const muiTheme = createTheme({
  palette: {
    mode: "light",
    primary: {
      main: "#4f5ff7",
      dark: "#3444dc",
      light: "#7f75ff",
    },
    background: {
      default: "#f6f8ff",
      paper: "#ffffff",
    },
    text: {
      primary: "#17203b",
      secondary: "#596783",
    },
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