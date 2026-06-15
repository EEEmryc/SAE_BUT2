import { useEffect, useState } from "react";
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
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import PersonOutlineRoundedIcon from "@mui/icons-material/PersonOutlineRounded";
import SaveRoundedIcon from "@mui/icons-material/SaveRounded";
import VisibilityOffOutlinedIcon from "@mui/icons-material/VisibilityOffOutlined";
import VisibilityOutlinedIcon from "@mui/icons-material/VisibilityOutlined";
import { Controller, useForm } from "react-hook-form";
import { useAuthStore } from "../../../../store/authStore";
import { getApiErrorMessage } from "../../../auth/services/apiError";
import { useUpdateProfile } from "../hooks/useUpdateProfile";
import {
  adminProfileSchema,
  type AdminProfileFormValues,
} from "../schemas/adminProfileSchema";

export function AdminProfileCard() {
  const user = useAuthStore((state) => state.user);
  const updateProfile = useUpdateProfile();
  const [showPassword, setShowPassword] = useState(false);

  const { control, handleSubmit, reset } = useForm<AdminProfileFormValues>({
    resolver: zodResolver(adminProfileSchema),
    defaultValues: { nom: "", prenom: "", password: "" },
    mode: "onTouched",
  });

  useEffect(() => {
    if (user) {
      reset({ nom: user.nom, prenom: user.prenom, password: "" });
    }
  }, [user, reset]);

  const submit = handleSubmit((values) => {
    updateProfile.mutate(
      {
        nom: values.nom,
        prenom: values.prenom,
        password: values.password || undefined,
      },
      {
        onSuccess: () => {
          reset({ nom: values.nom, prenom: values.prenom, password: "" });
        },
      },
    );
  });

  return (
    <Paper
      elevation={0}
      sx={{ p: 2.5, border: "1px solid #e1e6f2", borderRadius: 2.5 }}
    >
      <Typography sx={{ fontSize: 15.5, fontWeight: 850 }}>
        Mon profil
      </Typography>
      <Typography color="text.secondary" sx={{ fontSize: 13, mt: 0.5 }}>
        Modifiez votre nom, prénom et mot de passe.
      </Typography>

      {updateProfile.isError && (
        <Alert severity="error" sx={{ mt: 2 }}>
          {getApiErrorMessage(updateProfile.error)}
        </Alert>
      )}
      {updateProfile.isSuccess && (
        <Alert severity="success" sx={{ mt: 2 }}>
          Profil mis à jour avec succès.
        </Alert>
      )}

      <Box
        component="form"
        noValidate
        onSubmit={(event) => void submit(event)}
        sx={{
          mt: 1.5,
          display: "grid",
          gridTemplateColumns: { xs: "1fr", md: "repeat(2, 1fr)" },
          gap: 2.5,
        }}
      >
        <Controller
          name="nom"
          control={control}
          render={({ field, fieldState }) => (
            <TextField
              {...field}
              label="Nom"
              autoComplete="family-name"
              error={Boolean(fieldState.error)}
              helperText={fieldState.error?.message}
              slotProps={{
                input: {
                  startAdornment: (
                    <InputAdornment position="start">
                      <PersonOutlineRoundedIcon color="action" />
                    </InputAdornment>
                  ),
                },
              }}
            />
          )}
        />

        <Controller
          name="prenom"
          control={control}
          render={({ field, fieldState }) => (
            <TextField
              {...field}
              label="Prénom"
              autoComplete="given-name"
              error={Boolean(fieldState.error)}
              helperText={fieldState.error?.message}
              slotProps={{
                input: {
                  startAdornment: (
                    <InputAdornment position="start">
                      <PersonOutlineRoundedIcon color="action" />
                    </InputAdornment>
                  ),
                },
              }}
            />
          )}
        />

        <Controller
          name="password"
          control={control}
          render={({ field, fieldState }) => (
            <TextField
              {...field}
              label="Nouveau mot de passe"
              type={showPassword ? "text" : "password"}
              placeholder="Laisser vide pour ne pas changer"
              autoComplete="new-password"
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
                        edge="end"
                        aria-label={
                          showPassword
                            ? "Masquer le mot de passe"
                            : "Afficher le mot de passe"
                        }
                        onClick={() => setShowPassword((visible) => !visible)}
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
      </Box>

      <Box sx={{ mt: 2.5 }}>
        <Button
          type="submit"
          variant="contained"
          startIcon={<SaveRoundedIcon />}
          loading={updateProfile.isPending}
          onClick={(event) => void submit(event)}
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
