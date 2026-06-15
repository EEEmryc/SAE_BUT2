import { useState } from "react";
import {
  Alert,
  Box,
  Button,
  Checkbox,
  CircularProgress,
  FormControlLabel,
  Paper,
  Typography,
} from "@mui/material";
import SaveRoundedIcon from "@mui/icons-material/SaveRounded";
import { getApiErrorMessage } from "../../../auth/services/apiError";
import { useAppSettings } from "../hooks/useAppSettings";
import { useUpdateAppSettings } from "../hooks/useUpdateAppSettings";

const AVAILABLE_ROLES = [
  { value: "ETUDIANT", label: "Étudiant" },
  { value: "PROFESSEUR", label: "Professeur" },
];

export function RequestableRolesCard() {
  const settingsQuery = useAppSettings();
  const updateSettings = useUpdateAppSettings();
  const [overriddenRoles, setOverriddenRoles] = useState<string[] | null>(
    null,
  );
  const selectedRoles = overriddenRoles ?? settingsQuery.data?.requestableRoles ?? [];

  const toggleRole = (role: string) => {
    setOverriddenRoles(
      selectedRoles.includes(role)
        ? selectedRoles.filter((value) => value !== role)
        : [...selectedRoles, role],
    );
  };

  const submit = () => {
    updateSettings.mutate({ requestableRoles: selectedRoles });
  };

  return (
    <Paper
      elevation={0}
      sx={{ p: 2.5, border: "1px solid #e1e6f2", borderRadius: 2.5 }}
    >
      <Typography sx={{ fontSize: 15.5, fontWeight: 850 }}>
        Rôles demandables
      </Typography>
      <Typography color="text.secondary" sx={{ fontSize: 13, mt: 0.5 }}>
        Choisissez les types de compte que les visiteurs peuvent demander
        depuis le formulaire d'inscription.
      </Typography>

      {settingsQuery.isLoading ? (
        <Box sx={{ display: "flex", justifyContent: "center", py: 3 }}>
          <CircularProgress size={28} />
        </Box>
      ) : (
        <Box sx={{ mt: 1.5 }}>
          {AVAILABLE_ROLES.map((role) => (
            <FormControlLabel
              key={role.value}
              control={
                <Checkbox
                  checked={selectedRoles.includes(role.value)}
                  onChange={() => toggleRole(role.value)}
                />
              }
              label={role.label}
            />
          ))}
        </Box>
      )}

      {updateSettings.isError && (
        <Alert severity="error" sx={{ mt: 2 }}>
          {getApiErrorMessage(updateSettings.error)}
        </Alert>
      )}
      {updateSettings.isSuccess && (
        <Alert severity="success" sx={{ mt: 2 }}>
          Rôles demandables mis à jour.
        </Alert>
      )}

      <Box sx={{ mt: 2.5 }}>
        <Button
          variant="contained"
          startIcon={<SaveRoundedIcon />}
          loading={updateSettings.isPending}
          disabled={settingsQuery.isLoading}
          onClick={submit}
          sx={{
            px: 3,
            color: "#fff",
            background: "linear-gradient(110deg, #4056f4 0%, #7458f6 100%)",
            boxShadow: "0 12px 28px rgba(79,95,247,0.22)",
          }}
        >
          Enregistrer
        </Button>
      </Box>
    </Paper>
  );
}
