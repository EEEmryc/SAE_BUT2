import { zodResolver } from "@hookform/resolvers/zod";
import {
  Alert,
  Box,
  Button,
  Paper,
  TextField,
  Typography,
} from "@mui/material";
import LockResetRoundedIcon from "@mui/icons-material/LockResetRounded";
import { Controller, useForm } from "react-hook-form";
import { getApiErrorMessage } from "../../auth/services/apiError";
import { useChangePassword } from "../../auth/hooks/useChangePassword";
import {
  changePasswordSchema,
  type ChangePasswordFormValues,
} from "../../auth/schemas/changePasswordSchema";

const defaultValues: ChangePasswordFormValues = {
  currentPassword: "",
  newPassword: "",
  confirmation: "",
};

export function SettingsPage() {
  const changePassword = useChangePassword();

  const { control, handleSubmit, reset } = useForm<ChangePasswordFormValues>({
    resolver: zodResolver(changePasswordSchema),
    defaultValues,
    mode: "onTouched",
  });

  const submit = handleSubmit((values) => {
    changePassword.mutate(
      {
        currentPassword: values.currentPassword,
        newPassword: values.newPassword,
      },
      {
        onSuccess: () => reset(defaultValues),
      },
    );
  });

  return (
    <Box sx={{ maxWidth: 720, mx: "auto" }}>
      <Typography
        component="h1"
        sx={{
          fontSize: { xs: 30, sm: 38 },
          fontWeight: 850,
          letterSpacing: "-0.04em",
        }}
      >
        Paramètres
      </Typography>
      <Typography color="text.secondary" sx={{ mt: 0.5 }}>
        Gérez la sécurité de votre compte.
      </Typography>

      <Paper
        elevation={0}
        sx={{
          mt: 3,
          p: { xs: 2.5, sm: 3 },
          border: "1px solid",
          borderColor: "divider",
          borderRadius: 3.5,
        }}
      >
        <Box sx={{ display: "flex", alignItems: "center", gap: 1.5, mb: 2.5 }}>
          <LockResetRoundedIcon color="action" />
          <Box>
            <Typography sx={{ fontWeight: 800 }}>
              Modifier le mot de passe
            </Typography>
            <Typography color="text.secondary" sx={{ fontSize: 14 }}>
              Saisissez votre mot de passe actuel puis le nouveau.
            </Typography>
          </Box>
        </Box>

        <Box component="form" noValidate onSubmit={(e) => void submit(e)}>
          {changePassword.isError && (
            <Alert severity="error" sx={{ mb: 2.5 }}>
              {getApiErrorMessage(changePassword.error)}
            </Alert>
          )}
          {changePassword.isSuccess && (
            <Alert severity="success" sx={{ mb: 2.5 }}>
              Votre mot de passe a bien été modifié.
            </Alert>
          )}

          <Box sx={{ display: "grid", gap: 2.5 }}>
            <Controller
              name="currentPassword"
              control={control}
              render={({ field, fieldState }) => (
                <TextField
                  {...field}
                  type="password"
                  label="Mot de passe actuel"
                  error={Boolean(fieldState.error)}
                  helperText={fieldState.error?.message}
                />
              )}
            />
            <Controller
              name="newPassword"
              control={control}
              render={({ field, fieldState }) => (
                <TextField
                  {...field}
                  type="password"
                  label="Nouveau mot de passe"
                  error={Boolean(fieldState.error)}
                  helperText={fieldState.error?.message}
                />
              )}
            />
            <Controller
              name="confirmation"
              control={control}
              render={({ field, fieldState }) => (
                <TextField
                  {...field}
                  type="password"
                  label="Confirmer le nouveau mot de passe"
                  error={Boolean(fieldState.error)}
                  helperText={fieldState.error?.message}
                />
              )}
            />
          </Box>

          <Box sx={{ mt: 3 }}>
            <Button
              type="submit"
              variant="contained"
              loading={changePassword.isPending}
              sx={{ px: 3, color: "#fff" }}
            >
              Mettre à jour le mot de passe
            </Button>
          </Box>
        </Box>
      </Paper>
    </Box>
  );
}