import { zodResolver } from "@hookform/resolvers/zod";
import {
  Alert,
  Box,
  Button,
  IconButton,
  InputAdornment,
  Paper,
  TextField,
  Typography,
} from "@mui/material";
import LockResetRoundedIcon from "@mui/icons-material/LockResetRounded";
import VisibilityOffOutlinedIcon from "@mui/icons-material/VisibilityOffOutlined";
import VisibilityOutlinedIcon from "@mui/icons-material/VisibilityOutlined";
import { useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { Link, useSearchParams } from "react-router-dom";
import { getApiErrorMessage } from "../services/apiError";
import { BrandMark } from "../components/BrandMark";
import { useResetPassword } from "../hooks/useResetPassword";
import {
  resetPasswordSchema,
  type ResetPasswordFormValues,
} from "../schemas/authSchemas";

export function ResetPasswordPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token") ?? "";
  const [showPassword, setShowPassword] = useState(false);
  const resetPassword = useResetPassword();
  const { control, handleSubmit } = useForm<ResetPasswordFormValues>({
    resolver: zodResolver(resetPasswordSchema),
    defaultValues: { password: "", confirmation: "" },
    mode: "onTouched",
  });

  const submit = handleSubmit(({ password }) => {
    resetPassword.mutate({ token, password });
  });

  return (
    <Box className="login-page" sx={{ gridTemplateColumns: "1fr" }}>
      <Paper
        component="main"
        elevation={0}
        className="login-card"
        sx={{ maxWidth: 560 }}
      >
        <BrandMark compact />
        <LockResetRoundedIcon
          color="primary"
          sx={{ mt: 2.5, fontSize: 42 }}
        />
        <Typography
          component="h1"
          sx={{ mt: 1, fontSize: 32, fontWeight: 800 }}
        >
          Nouveau mot de passe
        </Typography>
        <Typography color="text.secondary" sx={{ mt: 1, mb: 3 }}>
          Choisissez un mot de passe sécurisé pour accéder à LearnHub.
        </Typography>

        {!token && (
          <Alert severity="error" sx={{ mb: 2, textAlign: "left" }}>
            Le lien de réinitialisation est incomplet ou invalide.
          </Alert>
        )}

        {resetPassword.isError && (
          <Alert severity="error" sx={{ mb: 2, textAlign: "left" }}>
            {getApiErrorMessage(resetPassword.error)}
          </Alert>
        )}

        {resetPassword.isSuccess ? (
          <>
            <Alert severity="success" sx={{ mb: 3, textAlign: "left" }}>
              Votre mot de passe a été modifié avec succès.
            </Alert>
            <Button
              component={Link}
              to="/login"
              variant="contained"
              fullWidth
            >
              Se connecter
            </Button>
          </>
        ) : (
          <Box
            component="form"
            noValidate
            onSubmit={(event) => void submit(event)}
          >
            <Controller
              name="password"
              control={control}
              render={({ field, fieldState }) => (
                <TextField
                  {...field}
                  label="Nouveau mot de passe"
                  type={showPassword ? "text" : "password"}
                  autoComplete="new-password"
                  error={Boolean(fieldState.error)}
                  helperText={fieldState.error?.message}
                  sx={{ mb: 2.5 }}
                  slotProps={{
                    input: {
                      endAdornment: (
                        <InputAdornment position="end">
                          <IconButton
                            edge="end"
                            aria-label={
                              showPassword
                                ? "Masquer le mot de passe"
                                : "Afficher le mot de passe"
                            }
                            onClick={() =>
                              setShowPassword((visible) => !visible)
                            }
                          >
                            {showPassword ? (
                              <VisibilityOffOutlinedIcon />
                            ) : (
                              <VisibilityOutlinedIcon />
                            )}
                          </IconButton>
                        </InputAdornment>
                      ),
                    },
                  }}
                />
              )}
            />
            <Controller
              name="confirmation"
              control={control}
              render={({ field, fieldState }) => (
                <TextField
                  {...field}
                  label="Confirmer le mot de passe"
                  type={showPassword ? "text" : "password"}
                  autoComplete="new-password"
                  error={Boolean(fieldState.error)}
                  helperText={fieldState.error?.message}
                />
              )}
            />
            <Button
              type="submit"
              variant="contained"
              fullWidth
              disabled={!token}
              loading={resetPassword.isPending}
              sx={{ mt: 3 }}
            >
              Enregistrer le mot de passe
            </Button>
          </Box>
        )}
      </Paper>
    </Box>
  );
}
