import { zodResolver } from "@hookform/resolvers/zod";
import {
  Alert,
  Box,
  Button,
  IconButton,
  InputAdornment,
  MenuItem,
  TextField,
} from "@mui/material";
import AlternateEmailRoundedIcon from "@mui/icons-material/AlternateEmailRounded";
import BadgeRoundedIcon from "@mui/icons-material/BadgeRounded";
import CloseRoundedIcon from "@mui/icons-material/CloseRounded";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import PersonOutlineRoundedIcon from "@mui/icons-material/PersonOutlineRounded";
import SaveRoundedIcon from "@mui/icons-material/SaveRounded";
import VisibilityOffOutlinedIcon from "@mui/icons-material/VisibilityOffOutlined";
import VisibilityOutlinedIcon from "@mui/icons-material/VisibilityOutlined";
import { useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { getApiErrorMessage } from "../../../auth/api/apiError";
import { useCreateUser } from "../hooks/useCreateUser";
import {
  createUserSchema,
  type CreateUserFormValues,
} from "../schemas/createUserSchema";

const defaultValues: CreateUserFormValues = {
  nom: "",
  prenom: "",
  email: "",
  password: "",
  role: "ETUDIANT",
  statut: "ACTIF",
};

export function CreateUserForm() {
  const navigate = useNavigate();
  const createUser = useCreateUser();
  const [showPassword, setShowPassword] = useState(false);
  const { control, handleSubmit, reset } =
    useForm<CreateUserFormValues>({
      resolver: zodResolver(createUserSchema),
      defaultValues,
      mode: "onTouched",
    });

  const submit = handleSubmit((values) => {
    createUser.mutate(values, {
      onSuccess: () => reset(defaultValues),
    });
  });

  return (
    <Box
      component="form"
      noValidate
      onSubmit={(event) => void submit(event)}
    >
      {createUser.isError && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {getApiErrorMessage(createUser.error)}
        </Alert>
      )}

      {createUser.isSuccess && (
        <Alert severity="success" sx={{ mb: 3 }}>
          Utilisateur créé avec succès.{" "}
          {createUser.data.invitationEmailSent
            ? "L'email de première connexion a été envoyé."
            : "Le compte est créé, mais l'email d'invitation n'a pas pu être envoyé."}
        </Alert>
      )}

      <Box
        sx={{
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
              placeholder="Ex. : Dupont"
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
              placeholder="Ex. : Marie"
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
          name="email"
          control={control}
          render={({ field, fieldState }) => (
            <TextField
              {...field}
              label="Adresse email"
              type="email"
              placeholder="Ex. : marie.dupont@learnhub.fr"
              autoComplete="email"
              error={Boolean(fieldState.error)}
              helperText={fieldState.error?.message}
              slotProps={{
                input: {
                  startAdornment: (
                    <InputAdornment position="start">
                      <AlternateEmailRoundedIcon color="action" />
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
              label="Mot de passe provisoire"
              type={showPassword ? "text" : "password"}
              placeholder="8 caractères minimum"
              autoComplete="new-password"
              error={Boolean(fieldState.error)}
              helperText={
                fieldState.error?.message ??
                "L'utilisateur pourra le modifier avec le lien reçu."
              }
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

        <Controller
          name="role"
          control={control}
          render={({ field, fieldState }) => (
            <TextField
              {...field}
              select
              label="Rôle"
              error={Boolean(fieldState.error)}
              helperText={fieldState.error?.message}
              slotProps={{
                input: {
                  startAdornment: (
                    <InputAdornment position="start">
                      <BadgeRoundedIcon color="action" />
                    </InputAdornment>
                  ),
                },
              }}
            >
              <MenuItem value="ADMIN">Administrateur</MenuItem>
              <MenuItem value="PROFESSEUR">Professeur</MenuItem>
              <MenuItem value="ETUDIANT">Étudiant</MenuItem>
            </TextField>
          )}
        />

        <Controller
          name="statut"
          control={control}
          render={({ field, fieldState }) => (
            <TextField
              {...field}
              select
              label="Statut"
              error={Boolean(fieldState.error)}
              helperText={fieldState.error?.message}
            >
              <MenuItem value="ACTIF">Actif</MenuItem>
              <MenuItem value="INACTIF">Inactif</MenuItem>
            </TextField>
          )}
        />
      </Box>

      <Alert
        severity="info"
        sx={{
          mt: 3,
          border: "1px solid rgba(79,95,247,0.12)",
          bgcolor: "rgba(79,95,247,0.06)",
        }}
      >
        Un email contenant un lien de réinitialisation valable une heure sera
        envoyé à l'utilisateur.
      </Alert>

      <Box
        sx={{
          mt: 3.5,
          display: "flex",
          flexWrap: "wrap",
          gap: 1.5,
        }}
      >
        <Button
          type="submit"
          variant="contained"
          loading={createUser.isPending}
          startIcon={<SaveRoundedIcon />}
          sx={{
            px: 3,
            color: "#fff",
            background:
              "linear-gradient(110deg, #4056f4 0%, #7458f6 100%)",
            boxShadow: "0 12px 28px rgba(79,95,247,0.22)",
          }}
        >
          Enregistrer
        </Button>
        <Button
          type="button"
          variant="text"
          color="inherit"
          startIcon={<CloseRoundedIcon />}
          onClick={() => navigate("/dashboard")}
          sx={{ px: 2.5, bgcolor: "#f1f3fb" }}
        >
          Annuler
        </Button>
      </Box>
    </Box>
  );
}
