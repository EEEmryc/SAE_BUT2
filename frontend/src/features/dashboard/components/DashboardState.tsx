import { Alert, Box, Button, CircularProgress } from "@mui/material";
import { getApiErrorMessage } from "../../auth/api/apiError";

export function DashboardLoading() {
  return (
    <Box sx={{ minHeight: 460, display: "grid", placeItems: "center" }}>
      <CircularProgress aria-label="Chargement du tableau de bord" />
    </Box>
  );
}

export function DashboardError({
  error,
  onRetry,
}: {
  error: unknown;
  onRetry: () => void;
}) {
  return (
    <Alert
      severity="error"
      action={
        <Button color="inherit" onClick={onRetry}>
          Réessayer
        </Button>
      }
    >
      Impossible de charger le tableau de bord. {getApiErrorMessage(error)}
    </Alert>
  );
}
