import { zodResolver } from "@hookform/resolvers/zod";
import {
  Alert,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Typography,
} from "@mui/material";
import { useEffect } from "react";
import { Controller, useForm } from "react-hook-form";
import { getApiErrorMessage } from "../api/apiError";
import { useForgotPassword } from "../hooks/useForgotPassword";
import {
  forgotPasswordSchema,
  type ForgotPasswordFormValues,
} from "../schemas/authSchemas";

type ForgotPasswordDialogProps = {
  open: boolean;
  initialEmail?: string;
  onClose: () => void;
};

export function ForgotPasswordDialog({
  open,
  initialEmail = "",
  onClose,
}: ForgotPasswordDialogProps) {
  const forgotPassword = useForgotPassword();
  const { control, handleSubmit, reset } =
    useForm<ForgotPasswordFormValues>({
      resolver: zodResolver(forgotPasswordSchema),
      defaultValues: { email: initialEmail },
    });

  useEffect(() => {
    if (open) {
      reset({ email: initialEmail });
      forgotPassword.reset();
    }
  }, [forgotPassword, initialEmail, open, reset]);

  const submit = handleSubmit(({ email }) => {
    forgotPassword.mutate(email);
  });

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="xs">
      <DialogTitle sx={{ fontWeight: 800 }}>
        Réinitialiser le mot de passe
      </DialogTitle>
      <DialogContent>
        <Typography color="text.secondary" sx={{ mb: 2 }}>
          Saisissez votre adresse email pour générer une demande de
          réinitialisation.
        </Typography>

        {forgotPassword.isError && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {getApiErrorMessage(forgotPassword.error)}
          </Alert>
        )}

        {forgotPassword.isSuccess ? (
          <Alert severity="success">
            {forgotPassword.data.message}
          </Alert>
        ) : (
          <Controller
            name="email"
            control={control}
            render={({ field, fieldState }) => (
              <TextField
                {...field}
                autoFocus
                label="Email"
                type="email"
                error={Boolean(fieldState.error)}
                helperText={fieldState.error?.message}
                onKeyDown={(event) => {
                  if (event.key === "Enter") {
                    void submit();
                  }
                }}
              />
            )}
          />
        )}
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 3 }}>
        <Button variant="text" onClick={onClose}>
          Fermer
        </Button>
        {!forgotPassword.isSuccess && (
          <Button
            variant="contained"
            onClick={() => void submit()}
            loading={forgotPassword.isPending}
          >
            Envoyer
          </Button>
        )}
      </DialogActions>
    </Dialog>
  );
}
