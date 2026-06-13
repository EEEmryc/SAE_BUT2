import { useEffect, useState } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import { Controller, useForm, useWatch } from "react-hook-form";
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  MenuItem,
  Paper,
  TextField,
  Typography,
} from "@mui/material";
import CloseRoundedIcon from "@mui/icons-material/CloseRounded";
import EditRoundedIcon from "@mui/icons-material/EditRounded";
import SendRoundedIcon from "@mui/icons-material/SendRounded";
import { getApiErrorMessage } from "../../auth/api/apiError";
import type { Message } from "../api/messagingApi";
import {
  useRecipients,
  useReplyToMessage,
  useSendMessage,
} from "../hooks/useMessaging";
import {
  messageSchema,
  type MessageFormValues,
} from "../schemas/messageSchema";

type MessageComposerProps = {
  replyTo: Message | null;
  onCancelReply: () => void;
  onSent: () => void;
};

const defaultValues: MessageFormValues = {
  emailDestinataire: "",
  sujet: "",
  contenu: "",
};

export function MessageComposer({
  replyTo,
  onCancelReply,
  onSent,
}: MessageComposerProps) {
  const recipients = useRecipients();
  const sendMessage = useSendMessage();
  const replyMessage = useReplyToMessage();
  const [sentSuccessfully, setSentSuccessfully] = useState(false);
  const isReply = replyTo !== null;

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<MessageFormValues>({
    resolver: zodResolver(messageSchema),
    defaultValues,
  });

  useEffect(() => {
    if (replyTo) {
      reset({
        emailDestinataire: replyTo.expediteurEmail,
        sujet: replyTo.sujet.toLowerCase().startsWith("re:")
          ? replyTo.sujet
          : `Re: ${replyTo.sujet}`,
        contenu: "",
      });
    }
  }, [replyTo, reset]);

  const content = useWatch({ control, name: "contenu" });
  const mutation = isReply ? replyMessage : sendMessage;

  const submit = handleSubmit(async (values) => {
    setSentSuccessfully(false);

    if (replyTo) {
      await replyMessage.mutateAsync({
        messageId: replyTo.id,
        contenu: values.contenu.trim(),
      });
    } else {
      await sendMessage.mutateAsync({
        emailDestinataire: values.emailDestinataire,
        sujet: values.sujet.trim(),
        contenu: values.contenu.trim(),
      });
    }

    reset(defaultValues);
    setSentSuccessfully(true);
    onCancelReply();
    onSent();
  });

  const cancelReply = () => {
    reset(defaultValues);
    setSentSuccessfully(false);
    onCancelReply();
  };

  return (
    <Paper
      component="section"
      elevation={0}
      sx={{
        minWidth: 0,
        p: { xs: 2.25, sm: 2.75 },
        border: "1px solid #e4e8f5",
        borderRadius: 3.5,
        boxShadow: "0 16px 42px rgba(48, 61, 124, 0.07)",
      }}
    >
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          gap: 1,
          mb: 2.5,
        }}
      >
        <Box sx={{ display: "flex", alignItems: "center", gap: 1.25 }}>
          <Box
            sx={{
              width: 44,
              height: 44,
              display: "grid",
              placeItems: "center",
              borderRadius: "50%",
              color: "primary.main",
              bgcolor: "rgba(79,95,247,0.1)",
            }}
          >
            <EditRoundedIcon />
          </Box>
          <Box>
            <Typography sx={{ fontSize: 18, fontWeight: 800 }}>
              {isReply ? "Répondre au message" : "Nouveau message"}
            </Typography>
            {isReply && (
              <Typography color="text.secondary" sx={{ fontSize: 12 }}>
                À {replyTo.expediteurPrenom} {replyTo.expediteurNom}
              </Typography>
            )}
          </Box>
        </Box>

        {isReply && (
          <Button
            size="small"
            color="inherit"
            startIcon={<CloseRoundedIcon />}
            onClick={cancelReply}
          >
            Annuler
          </Button>
        )}
      </Box>

      {sentSuccessfully && (
        <Alert severity="success" sx={{ mb: 2 }}>
          Message envoyé avec succès.
        </Alert>
      )}
      {mutation.isError && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {getApiErrorMessage(mutation.error)}
        </Alert>
      )}
      {recipients.isError && !isReply && (
        <Alert severity="error" sx={{ mb: 2 }}>
          Impossible de charger les destinataires.
        </Alert>
      )}

      <Box component="form" noValidate onSubmit={submit}>
        <Controller
          name="emailDestinataire"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              select={!isReply}
              disabled={isReply || recipients.isLoading}
              label="Destinataire"
              error={Boolean(errors.emailDestinataire)}
              helperText={errors.emailDestinataire?.message}
              sx={{ mb: 2 }}
            >
              {!isReply &&
                recipients.data?.map((recipient) => (
                  <MenuItem key={recipient.id} value={recipient.email}>
                    {recipient.prenom} {recipient.nom} · {recipient.email}
                  </MenuItem>
                ))}
            </TextField>
          )}
        />

        <Controller
          name="sujet"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              disabled={isReply}
              label="Sujet"
              placeholder="Sujet du message"
              error={Boolean(errors.sujet)}
              helperText={errors.sujet?.message}
              sx={{ mb: 2 }}
            />
          )}
        />

        <Controller
          name="contenu"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              multiline
              minRows={7}
              label={isReply ? "Votre réponse" : "Message"}
              placeholder="Écrivez votre message ici..."
              error={Boolean(errors.contenu)}
              helperText={errors.contenu?.message}
              slotProps={{
                htmlInput: { maxLength: 2000 },
              }}
            />
          )}
        />

        <Typography
          color="text.secondary"
          sx={{ mt: 0.7, textAlign: "right", fontSize: 12 }}
        >
          {content.length} / 2000
        </Typography>

        <Button
          fullWidth
          type="submit"
          variant="contained"
          disabled={mutation.isPending}
          startIcon={
            mutation.isPending ? (
              <CircularProgress size={18} color="inherit" />
            ) : (
              <SendRoundedIcon />
            )
          }
          sx={{
            mt: 2.5,
            background: "linear-gradient(135deg, #4f5ff7, #7255ef)",
          }}
        >
          {mutation.isPending ? "Envoi..." : "Envoyer"}
        </Button>
      </Box>
    </Paper>
  );
}
