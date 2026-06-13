import {
  Avatar,
  Box,
  Button,
  Chip,
  CircularProgress,
  Divider,
  Paper,
  Typography,
} from "@mui/material";
import InboxRoundedIcon from "@mui/icons-material/InboxRounded";
import MarkEmailReadRoundedIcon from "@mui/icons-material/MarkEmailReadRounded";
import ReplyRoundedIcon from "@mui/icons-material/ReplyRounded";
import type { Message } from "../api/messagingApi";

type MessageDetailProps = {
  message?: Message;
  folder: "inbox" | "sent";
  loading?: boolean;
  onReply: (message: Message) => void;
  onMarkAsRead: (messageId: number) => void;
};

function formatLongDate(value: string) {
  return new Intl.DateTimeFormat("fr-FR", {
    dateStyle: "long",
    timeStyle: "short",
  }).format(new Date(value));
}

export function MessageDetail({
  message,
  folder,
  loading,
  onReply,
  onMarkAsRead,
}: MessageDetailProps) {
  if (loading) {
    return (
      <Paper
        elevation={0}
        sx={{
          minHeight: 500,
          display: "grid",
          placeItems: "center",
          border: "1px solid #e4e8f5",
          borderRadius: 3.5,
        }}
      >
        <CircularProgress size={32} />
      </Paper>
    );
  }

  if (!message) {
    return (
      <Paper
        elevation={0}
        sx={{
          minHeight: 500,
          px: 4,
          display: "grid",
          placeItems: "center",
          textAlign: "center",
          border: "1px solid #e4e8f5",
          borderRadius: 3.5,
          boxShadow: "0 16px 42px rgba(48, 61, 124, 0.07)",
        }}
      >
        <Box>
          <Box
            sx={{
              width: 62,
              height: 62,
              mx: "auto",
              display: "grid",
              placeItems: "center",
              borderRadius: "50%",
              bgcolor: "rgba(79,95,247,0.09)",
              color: "primary.main",
            }}
          >
            <InboxRoundedIcon sx={{ fontSize: 30 }} />
          </Box>
          <Typography sx={{ mt: 2, fontWeight: 800 }}>
            Sélectionnez un message
          </Typography>
          <Typography color="text.secondary" sx={{ mt: 0.5, fontSize: 14 }}>
            Son contenu complet apparaîtra ici.
          </Typography>
        </Box>
      </Paper>
    );
  }

  const participant =
    folder === "inbox"
      ? {
          nom: message.expediteurNom,
          prenom: message.expediteurPrenom,
          email: message.expediteurEmail,
        }
      : {
          nom: message.destinataireNom,
          prenom: message.destinatairePrenom,
          email: message.destinataireEmail,
        };
  const initials =
    `${participant.prenom[0] ?? ""}${participant.nom[0] ?? ""}`.toUpperCase();

  return (
    <Paper
      data-testid="message-detail"
      elevation={0}
      sx={{
        minWidth: 0,
        minHeight: 500,
        p: { xs: 2.25, sm: 3 },
        border: "1px solid #e4e8f5",
        borderRadius: 3.5,
        boxShadow: "0 16px 42px rgba(48, 61, 124, 0.07)",
      }}
    >
      <Box
        sx={{
          display: "flex",
          flexWrap: "wrap",
          justifyContent: "space-between",
          alignItems: "center",
          gap: 1.25,
        }}
      >
        <Box sx={{ display: "flex", gap: 1 }}>
          {folder === "inbox" && (
            <Button
              size="small"
              variant="outlined"
              startIcon={<ReplyRoundedIcon />}
              onClick={() => onReply(message)}
            >
              Répondre
            </Button>
          )}
          {folder === "inbox" && !message.lu && (
            <Button
              size="small"
              variant="text"
              startIcon={<MarkEmailReadRoundedIcon />}
              onClick={() => onMarkAsRead(message.id)}
            >
              Marquer comme lu
            </Button>
          )}
        </Box>
        <Chip
          size="small"
          label={message.lu ? "Lu" : "Non lu"}
          color={message.lu ? "default" : "primary"}
          variant={message.lu ? "outlined" : "filled"}
        />
      </Box>

      <Box sx={{ mt: 3, display: "flex", gap: 1.5, alignItems: "center" }}>
        <Avatar
          sx={{
            width: 48,
            height: 48,
            fontWeight: 800,
            color: "primary.main",
            bgcolor: "rgba(79,95,247,0.11)",
          }}
        >
          {initials}
        </Avatar>
        <Box sx={{ minWidth: 0 }}>
          <Typography sx={{ fontSize: 17, fontWeight: 800 }}>
            {participant.prenom} {participant.nom}
          </Typography>
          <Typography noWrap color="text.secondary" sx={{ fontSize: 13 }}>
            {participant.email}
          </Typography>
        </Box>
      </Box>

      <Box sx={{ mt: 2.5 }}>
        <Typography color="text.secondary" sx={{ fontSize: 12.5 }}>
          {folder === "inbox" ? "De" : "À"} : {participant.email}
        </Typography>
        <Typography sx={{ mt: 1, fontSize: 19, fontWeight: 800 }}>
          {message.sujet}
        </Typography>
        <Typography color="text.secondary" sx={{ mt: 0.6, fontSize: 12.5 }}>
          {formatLongDate(message.dateEnvoi)}
        </Typography>
      </Box>

      <Divider sx={{ my: 2.5 }} />

      <Typography
        component="div"
        sx={{
          color: "#303a55",
          fontSize: 14.5,
          lineHeight: 1.8,
          whiteSpace: "pre-wrap",
          overflowWrap: "anywhere",
        }}
      >
        {message.contenu}
      </Typography>
    </Paper>
  );
}
