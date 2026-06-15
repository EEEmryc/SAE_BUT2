import { zodResolver } from "@hookform/resolvers/zod";
import {
  Alert,
  Box,
  Button,
  InputAdornment,
  Link,
  MenuItem,
  Paper,
  TextField,
  Typography,
} from "@mui/material";
import ArrowBackRoundedIcon from "@mui/icons-material/ArrowBackRounded";
import CheckRoundedIcon from "@mui/icons-material/CheckRounded";
import EmailOutlinedIcon from "@mui/icons-material/EmailOutlined";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import PersonOutlineRoundedIcon from "@mui/icons-material/PersonOutlineRounded";
import SchoolOutlinedIcon from "@mui/icons-material/SchoolOutlined";
import SendRoundedIcon from "@mui/icons-material/SendRounded";
import GroupsOutlinedIcon from "@mui/icons-material/GroupsOutlined";
import ChatBubbleOutlineRoundedIcon from "@mui/icons-material/ChatBubbleOutlineRounded";
import { Controller, useForm } from "react-hook-form";
import type { Control } from "react-hook-form";
import type { ReactNode } from "react";
import { Link as RouterLink } from "react-router-dom";
import { BrandMark } from "../../auth/components/BrandMark";
import { getApiErrorMessage } from "../../auth/services/apiError";
import { useSubmitAccountRequest } from "../hooks/useAccountRequests";
import {
  accountRequestSchema,
  type AccountRequestFormValues,
} from "../schemas/accountRequestSchema";

const defaultValues: AccountRequestFormValues = {
  nom: "",
  prenom: "",
  email: "",
  formation: "",
  requestedRole: "ETUDIANT",
  commentaire: "",
};

export function AccountRequestPage() {
  const submitRequest = useSubmitAccountRequest();
  const {
    control,
    handleSubmit,
    formState: { isValid },
  } = useForm<AccountRequestFormValues>({
    resolver: zodResolver(accountRequestSchema),
    defaultValues,
    mode: "onChange",
  });

  if (submitRequest.isSuccess) {
    return (
      <Box className="account-request-page">
        <Paper className="account-request-success" elevation={0}>
          <Box className="account-request-header">
            <BrandMark />
            <Button
              component={RouterLink}
              to="/login"
              color="inherit"
              startIcon={<ArrowBackRoundedIcon />}
            >
              Retour à la connexion
            </Button>
          </Box>
          <Box className="account-request-success-content">
            <Box className="account-request-check">
              <CheckRoundedIcon />
            </Box>
            <Typography component="h1" sx={{ mt: 3, fontSize: 34, fontWeight: 850 }}>
              Demande envoyée avec succès !
            </Typography>
            <Typography color="text.secondary" sx={{ mt: 1.5, maxWidth: 620, fontSize: 17 }}>
              Votre demande de création de compte a bien été envoyée. Un
              administrateur l’examinera prochainement.
            </Typography>
            <Paper elevation={0} sx={{ mt: 4, width: "min(100%, 620px)", p: 3, border: "1px solid #e4e7f4" }}>
              <Typography sx={{ fontWeight: 800 }}>
                {submitRequest.data.confirmationEmailSent
                  ? "E-mail de confirmation envoyé"
                  : "Demande enregistrée"}
              </Typography>
              <Typography color="text.secondary" sx={{ mt: 0.5 }}>
                {submitRequest.data.confirmationEmailSent
                  ? "Vérifiez votre boîte de réception."
                  : "L’e-mail n’a pas pu être envoyé, mais votre demande sera bien traitée."}
              </Typography>
            </Paper>
            <Button
              component={RouterLink}
              to="/login"
              variant="contained"
              sx={{ mt: 4, px: 5, color: "#fff" }}
            >
              Retour à la page de connexion
            </Button>
          </Box>
        </Paper>
      </Box>
    );
  }

  return (
    <Box className="account-request-page">
      <Paper className="account-request-card" elevation={0}>
        <Box className="account-request-header">
          <BrandMark />
          <Link
            component={RouterLink}
            to="/login"
            underline="hover"
            sx={{ display: "flex", alignItems: "center", gap: 0.75, fontWeight: 700 }}
          >
            <ArrowBackRoundedIcon fontSize="small" />
            Retour à la connexion
          </Link>
        </Box>

        <Box sx={{ maxWidth: 760, mx: "auto", py: { xs: 3, sm: 5 } }}>
          <Typography component="h1" sx={{ fontSize: { xs: 30, sm: 38 }, fontWeight: 850 }}>
            Demande de création de compte
          </Typography>
          <Typography color="text.secondary" sx={{ mt: 1, fontSize: 17, lineHeight: 1.6 }}>
            Remplissez ce formulaire. Un administrateur examinera votre demande
            avant de créer votre compte LearnHub.
          </Typography>

          <Box
            component="form"
            onSubmit={(event) =>
              void handleSubmit((values) => submitRequest.mutate(values))(event)
            }
            noValidate
            sx={{ mt: 4 }}
          >
            {submitRequest.isError && (
              <Alert severity="error" sx={{ mb: 2.5 }}>
                {getApiErrorMessage(submitRequest.error)}
              </Alert>
            )}

            <Box sx={{ display: "grid", gridTemplateColumns: { xs: "1fr", sm: "1fr 1fr" }, gap: 2.25 }}>
              <RequestField name="nom" label="Nom" control={control} icon={<PersonOutlineRoundedIcon />} />
              <RequestField name="prenom" label="Prénom" control={control} icon={<PersonOutlineRoundedIcon />} />
            </Box>
            <Box sx={{ mt: 2.25 }}>
              <RequestField name="email" label="Adresse e-mail" type="email" control={control} icon={<EmailOutlinedIcon />} />
            </Box>
            <Box sx={{ mt: 2.25 }}>
              <RequestField name="formation" label="Diplôme / Formation" control={control} icon={<SchoolOutlinedIcon />} />
            </Box>

            <Controller
              name="requestedRole"
              control={control}
              render={({ field, fieldState }) => (
                <TextField
                  {...field}
                  select
                  label="Type de compte demandé"
                  error={Boolean(fieldState.error)}
                  helperText={fieldState.error?.message}
                  sx={{ mt: 2.25 }}
                  slotProps={{
                    input: {
                      startAdornment: (
                        <InputAdornment position="start">
                          <GroupsOutlinedIcon color="action" />
                        </InputAdornment>
                      ),
                    },
                  }}
                >
                  <MenuItem value="ETUDIANT">Étudiant</MenuItem>
                  <MenuItem value="PROFESSEUR">Professeur</MenuItem>
                </TextField>
              )}
            />

            <Controller
              name="commentaire"
              control={control}
              render={({ field, fieldState }) => (
                <TextField
                  {...field}
                  multiline
                  minRows={4}
                  label="Commentaire / Motif de la demande"
                  placeholder="Expliquez la raison de votre demande d’inscription..."
                  error={Boolean(fieldState.error)}
                  helperText={fieldState.error?.message}
                  sx={{ mt: 2.25 }}
                  slotProps={{
                    input: {
                      startAdornment: (
                        <InputAdornment position="start" sx={{ alignSelf: "flex-start", mt: 1.25 }}>
                          <ChatBubbleOutlineRoundedIcon color="action" />
                        </InputAdornment>
                      ),
                    },
                  }}
                />
              )}
            />

            <Button
              type="submit"
              variant="contained"
              fullWidth
              disabled={!isValid || submitRequest.isPending}
              loading={submitRequest.isPending}
              startIcon={<SendRoundedIcon />}
              sx={{
                mt: 3.5,
                color: "#fff",
                background: "linear-gradient(110deg, #5364f4, #8c5cf6)",
              }}
            >
              Envoyer la demande
            </Button>
            <Box sx={{ mt: 2.5, display: "flex", gap: 1, alignItems: "center", color: "text.secondary" }}>
              <LockOutlinedIcon sx={{ fontSize: 18 }} />
              <Typography sx={{ fontSize: 13 }}>
                Vos informations sont utilisées uniquement pour traiter votre demande.
              </Typography>
            </Box>
          </Box>
        </Box>
      </Paper>
    </Box>
  );
}

type RequestFieldProps = {
  name: "nom" | "prenom" | "email" | "formation";
  label: string;
  type?: string;
  control: Control<AccountRequestFormValues>;
  icon: ReactNode;
};

function RequestField({ name, label, type, control, icon }: RequestFieldProps) {
  return (
    <Controller
      name={name}
      control={control}
      render={({ field, fieldState }) => (
        <TextField
          {...field}
          label={label}
          type={type}
          error={Boolean(fieldState.error)}
          helperText={fieldState.error?.message}
          slotProps={{
            input: {
              startAdornment: (
                <InputAdornment position="start">{icon}</InputAdornment>
              ),
            },
          }}
        />
      )}
    />
  );
}
