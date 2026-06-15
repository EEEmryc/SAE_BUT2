import { useEffect, useMemo, type PropsWithChildren } from "react";
import {
  Box,
  CircularProgress,
  CssBaseline,
  ThemeProvider,
} from "@mui/material";
import { QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter } from "react-router-dom";
import { queryClient } from "./queryClient";
import { createAppTheme } from "../../theme/muiTheme";
import { useAuthStore } from "../../store/authStore";
import { useThemeStore } from "../../store/themeStore";

function SessionRestorer({ children }: PropsWithChildren) {
  const isRestoring = useAuthStore((state) => state.isRestoring);
  const restoreSession = useAuthStore((state) => state.restoreSession);

  useEffect(() => {
    void restoreSession();
  }, [restoreSession]);

  if (isRestoring) {
    return (
      <Box
        sx={{
          minHeight: "100vh",
          display: "grid",
          placeItems: "center",
          bgcolor: "background.default",
        }}
      >
        <CircularProgress aria-label="Restauration de la session" />
      </Box>
    );
  }

  return children;
}

export function AppProviders({ children }: PropsWithChildren) {
  const mode = useThemeStore((state) => state.mode);
  const theme = useMemo(() => createAppTheme(mode), [mode]);

  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <BrowserRouter>
          <SessionRestorer>{children}</SessionRestorer>
        </BrowserRouter>
      </ThemeProvider>
    </QueryClientProvider>
  );
}