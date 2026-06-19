import { useMemo, useState } from "react";
import {
  Avatar,
  Badge,
  Box,
  CircularProgress,
  InputAdornment,
  List,
  ListItemButton,
  Paper,
  TextField,
  Typography,
} from "@mui/material";
import SearchRoundedIcon from "@mui/icons-material/SearchRounded";
import type { Message } from "../services/messagingApi";

type MessageListProps = {
  messages: Message[];
  selectedId: number | null;
  folder: "inbox" | "sent";
  loading?: boolean;
  onSelect: (messageId: number) => void;
};

const avatarColors = ["#5865f2", "#1aa981", "#e99b19", "#d94ca8", "#6f55d9"];

function formatDate(value: string) {
  const date = new Date(value);
  const today = new Date();

  if (date.toDateString() === today.toDateString()) {
    return new Intl.DateTimeFormat("fr-FR", {
      hour: "2-digit",
      minute: "2-digit",
    }).format(date);
  }

  return new Intl.DateTimeFormat("fr-FR", {
    day: "2-digit",
    month: "2-digit",
  }).format(date);
}

export function MessageList({
  messages,
  selectedId,
  folder,
  loading,
  onSelect,
}: MessageListProps) {
  const [search, setSearch] = useState("");
  const normalizedSearch = search.trim().toLocaleLowerCase("fr");

  const filteredMessages = useMemo(
    () =>
      messages.filter((message) => {
        const participant =
          folder === "inbox"
            ? `${message.expediteurPrenom} ${message.expediteurNom} ${message.expediteurEmail}`
            : `${message.destinatairePrenom} ${message.destinataireNom} ${message.destinataireEmail}`;
        return `${participant} ${message.sujet} ${message.contenu}`
          .toLocaleLowerCase("fr")
          .includes(normalizedSearch);
      }),
    [folder, messages, normalizedSearch],
  );

  return (
    <Paper
      elevation={0}
      sx={{
        minWidth: 0,
        overflow: "hidden",
        border: "1px solid #e4e8f5",
        borderRadius: 3.5,
        boxShadow: "0 16px 42px rgba(48, 61, 124, 0.07)",
      }}
    >
      <Box sx={{ p: 1.75, borderBottom: "1px solid #edf0f8" }}>
        <TextField
          size="small"
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          placeholder="Rechercher un message..."
          aria-label="Rechercher un message"
          slotProps={{
            input: {
              startAdornment: (
                <InputAdornment position="start">
                  <SearchRoundedIcon fontSize="small" />
                </InputAdornment>
              ),
            },
          }}
        />
      </Box>

      {loading ? (
        <Box sx={{ minHeight: 360, display: "grid", placeItems: "center" }}>
          <CircularProgress size={30} />
        </Box>
      ) : filteredMessages.length === 0 ? (
        <Box
          sx={{
            minHeight: 360,
            px: 3,
            display: "grid",
            placeItems: "center",
            textAlign: "center",
          }}
        >
          <Box>
            <Typography sx={{ fontWeight: 750 }}>
              Aucun message à afficher
            </Typography>
            <Typography color="text.secondary" sx={{ mt: 0.5, fontSize: 14 }}>
              {search
                ? "Aucun résultat ne correspond à votre recherche."
                : folder === "inbox"
                  ? "Votre boîte de réception est vide."
                  : "Vous n'avez encore envoyé aucun message."}
            </Typography>
          </Box>
        </Box>
      ) : (
        <List disablePadding sx={{ maxHeight: { md: 620 }, overflowY: "auto" }}>
          {filteredMessages.map((message, index) => {
            const participant =
              folder === "inbox"
                ? {
                    nom: message.expediteurNom,
                    prenom: message.expediteurPrenom,
                  }
                : {
                    nom: message.destinataireNom,
                    prenom: message.destinatairePrenom,
                  };
            const initials =
              `${participant.prenom[0] ?? ""}${participant.nom[0] ?? ""}`.toUpperCase();
            const unread = folder === "inbox" && !message.lu;

            return (
              <ListItemButton
                key={message.id}
                selected={selectedId === message.id}
                onClick={() => onSelect(message.id)}
                sx={{
                  alignItems: "flex-start",
                  gap: 1.5,
                  px: 2,
                  py: 1.7,
                  borderBottom: "1px solid #edf0f8",
                  "&.Mui-selected": {
                    bgcolor: "rgba(79,95,247,0.09)",
                    "&:hover": { bgcolor: "rgba(79,95,247,0.12)" },
                  },
                }}
              >
                <Badge
                  color="primary"
                  variant="dot"
                  invisible={!unread}
                  overlap="circular"
                >
                  <Avatar
                    sx={{
                      width: 42,
                      height: 42,
                      fontSize: 14,
                      fontWeight: 800,
                      bgcolor: `${avatarColors[index % avatarColors.length]}18`,
                      color: avatarColors[index % avatarColors.length],
                    }}
                  >
                    {initials}
                  </Avatar>
                </Badge>

                <Box sx={{ minWidth: 0, flex: 1 }}>
                  <Box sx={{ display: "flex", gap: 1, alignItems: "center" }}>
                    <Typography
                      noWrap
                      sx={{ flex: 1, fontSize: 14, fontWeight: unread ? 800 : 650 }}
                    >
                      {participant.prenom} {participant.nom}
                    </Typography>
                    <Typography color="text.secondary" sx={{ fontSize: 11 }}>
                      {formatDate(message.dateEnvoi)}
                    </Typography>
                  </Box>
                  <Typography
                    noWrap
                    sx={{ mt: 0.3, fontSize: 13, fontWeight: unread ? 750 : 600 }}
                  >
                    {message.sujet}
                  </Typography>
                  <Typography
                    noWrap
                    color="text.secondary"
                    sx={{ mt: 0.35, fontSize: 12 }}
                  >
                    {message.contenu}
                  </Typography>
                </Box>
              </ListItemButton>
            );
          })}
        </List>
      )}

      <Box sx={{ px: 2.25, py: 1.5, borderTop: "1px solid #edf0f8" }}>
        <Typography color="text.secondary" sx={{ fontSize: 12.5 }}>
          {filteredMessages.length} message
          {filteredMessages.length > 1 ? "s" : ""}
        </Typography>
      </Box>
    </Paper>
  );
}
