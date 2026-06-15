import { zodResolver } from "@hookform/resolvers/zod";
import {
  Alert,
  Box,
  Button,
  IconButton,
  InputAdornment,
  Link,
  TextField,
  Typography,
} from "@mui/material";
import EmailOutlinedIcon from "@mui/icons-material/EmailOutlined";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import VisibilityOutlinedIcon from "@mui/icons-material/VisibilityOutlined";
import VisibilityOffOutlinedIcon from "@mui/icons-material/VisibilityOffOutlined";
import { useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { getApiErrorMessage } from "../services/apiError";
import { useLogin } from "../hooks/useLogin";
import { loginSchema, type LoginFormValues } from "../schemas/authSchemas";
import { ForgotPasswordDialog } from "./ForgotPasswordDialog";

export function LoginForm() {
  const [showPassword, setShowPassword] = useState(false);
  const [forgotOpen, setForgotOpen] = useState(false);
  const login = useLogin();
  const { control, handleSubmit, getValues } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: "",
      password: "",
    },
    mode: "onTouched",
  });

  const submit = handleSubmit((values) => login.mutate(values));

  return (
    <>
      <Box
        component="form"
        noValidate
        onSubmit={(event) => void submit(event)}
        sx={{ mt: 3.5 }}
      >
        {login.isError && (
          <Alert severity="error" sx={{ mb: 2.5 }}>
            {getApiErrorMessage(login.error)}
          </Alert>
        )}

        <Controller
          name="email"
          control={control}
          render={({ field, fieldState }) => (
            <TextField
              {...field}
              label="Email"
              type="email"
              autoComplete="email"
              placeholder="Entrez votre email"
              error={Boolean(fieldState.error)}
              helperText={fieldState.error?.message}
              slotProps={{
                input: {
                  startAdornment: (
                    <InputAdornment position="start">
                      <EmailOutlinedIcon color="action" />
                    </InputAdornment>
                  ),
                },
              }}
              sx={{ mb: 2.5 }}
            />
          )}
        />

        <Controller
          name="password"
          control={control}
          render={({ field, fieldState }) => (
            <TextField
              {...field}
              label="Mot de passe"
              type={showPassword ? "text" : "password"}
              autoComplete="current-password"
              placeholder="Entrez votre mot de passe"
              error={Boolean(fieldState.error)}
              helperText={fieldState.error?.message}
              slotProps={{
                input: {
                  startAdornment: (
                    <InputAdornment position="start">
                      <LockOutlinedIcon color="action" />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        aria-label={
                          showPassword
                            ? "Masquer le mot de passe"
                            : "Afficher le mot de passe"
                        }
                        onClick={() => setShowPassword((visible) => !visible)}
                        edge="end"
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

        <Box sx={{ display: "flex", justifyContent: "flex-end", mt: 1 }}>
          <Link
            component="button"
            type="button"
            underline="hover"
            onClick={() => setForgotOpen(true)}
            sx={{ fontWeight: 600 }}
          >
            Mot de passe oublié ?
          </Link>
        </Box>

        <Button
          type="submit"
          variant="contained"
          fullWidth
          loading={login.isPending}
          sx={{
            mt: 3.5,
            color: "#fff",
            background:
              "linear-gradient(110deg, #3f55f4 0%, #7758f8 70%, #985cf6 100%)",
            boxShadow: "0 14px 30px rgba(79, 95, 247, 0.25)",
            "&:hover": {
              boxShadow: "0 16px 34px rgba(79, 95, 247, 0.34)",
            },
          }}
        >
          Se connecter
        </Button>

        <Typography
          align="center"
          color="text.secondary"
          sx={{ mt: 4, fontSize: 15 }}
        >
          Pas encore de compte ?{" "}
          <Link href="/register" underline="hover" sx={{ fontWeight: 700 }}>
            Créer un compte
          </Link>
        </Typography>
      </Box>

      <ForgotPasswordDialog
        open={forgotOpen}
        initialEmail={getValues("email")}
        onClose={() => setForgotOpen(false)}
      />
    </>
  );
}
