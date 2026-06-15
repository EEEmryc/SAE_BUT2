import {
  Alert,
  Box,
  CircularProgress,
  FormControlLabel,
  Paper,
  Switch,
  Typography,
} from "@mui/material";
import { getApiErrorMessage } from "../../../auth/services/apiError";
import { useAppSettings } from "../hooks/useAppSettings";
import { useUpdateAppSettings } from "../hooks/useUpdateAppSettings";

export function InscriptionValidationCard() {
  const settingsQuery = useAppSettings();
  const updateSettings = useUpdateAppSettings();

  const autoValidation = settingsQuery.data?.inscriptionAutoValidation ?? false;

  return (
    <Paper
      elevation={0}
      sx={{ p: 2.5, border: "1px solid #e1e6f2", borderRadius: 2.5 }}
    >
      <Typography sx={{ fontSize: 15.5, fontWeight: 850 }}>
        Validation des inscriptions
      </Typography>
      <Typography color="text.secondary" sx={{ fontSize: 13, mt: 0.5 }}>
        Définissez si les demandes d'inscription des étudiants à un cours
        sont validées automatiquement ou nécessitent l'accord du professeur.
      </Typography>

      {settingsQuery.isLoading ? (
        <Box sx={{ display: "flex", justifyContent: "center", py: 3 }}>
          <CircularProgress size={28} />
        </Box>
      ) : (
        <FormControlLabel
          sx={{ mt: 1.5 }}
          control={
            <Switch
              checked={autoValidation}
              onChange={(event) =>
                updateSettings.mutate({
                  inscriptionAutoValidation: event.target.checked,
                })
              }
            />
          }
          label={
            autoValidation
              ? "Validation automatique des inscriptions"
              : "Validation manuelle par le professeur"
          }
        />
      )}

      {updateSettings.isError && (
        <Alert severity="error" sx={{ mt: 2 }}>
          {getApiErrorMessage(updateSettings.error)}
        </Alert>
      )}
    </Paper>
  );
}
