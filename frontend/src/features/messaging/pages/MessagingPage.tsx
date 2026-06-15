import { useRef, useState } from "react";
import {
  Alert,
  Badge,
  Box,
  Button,
  Tab,
  Tabs,
  Typography,
} from "@mui/material";
import AddRoundedIcon from "@mui/icons-material/AddRounded";
import InboxRoundedIcon from "@mui/icons-material/InboxRounded";
import OutboxRoundedIcon from "@mui/icons-material/OutboxRounded";
import {
  useInbox,
  useMarkMessageAsRead,
  useMessageDetail,
  useOutbox,
  useUnreadCount,
} from "../hooks/useMessaging";
import type { Message } from "../services/messagingApi";
import { MessageComposer } from "../components/MessageComposer";
import { MessageDetail } from "../components/MessageDetail";
import { MessageList } from "../components/MessageList";

type Folder = "inbox" | "sent";

export function MessagingPage() {
  const [folder, setFolder] = useState<Folder>("inbox");
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [replyTo, setReplyTo] = useState<Message | null>(null);
  const composerRef = useRef<HTMLDivElement | null>(null);
  const inbox = useInbox();
  const outbox = useOutbox();
  const unread = useUnreadCount();

  const currentQuery = folder === "inbox" ? inbox : outbox;
  const messages = currentQuery.data ?? [];
  const effectiveSelectedId = messages.some(
    (message) => message.id === selectedId,
  )
    ? selectedId
    : (messages[0]?.id ?? null);
  const detail = useMessageDetail(effectiveSelectedId);
  const markAsRead = useMarkMessageAsRead();

  const showComposer = () => {
    setReplyTo(null);
    composerRef.current?.scrollIntoView({
      behavior: "smooth",
      block: "start",
    });
  };

  const startReply = (message: Message) => {
    setReplyTo(message);
    composerRef.current?.scrollIntoView({
      behavior: "smooth",
      block: "start",
    });
  };

  return (
    <Box sx={{ maxWidth: 1600, mx: "auto" }}>
      <Box
        sx={{
          display: "flex",
          flexWrap: "wrap",
          justifyContent: "space-between",
          alignItems: "flex-end",
          gap: 2,
        }}
      >
        <Box>
          <Typography
            component="h1"
            sx={{
              fontSize: { xs: 30, sm: 38 },
              fontWeight: 850,
              letterSpacing: "-0.04em",
            }}
          >
            Messagerie
          </Typography>
          <Typography color="text.secondary" sx={{ mt: 0.5 }}>
            Consultez vos échanges et communiquez avec les utilisateurs de
            LearnHub.
          </Typography>
        </Box>

        <Button
          variant="contained"
          startIcon={<AddRoundedIcon />}
          onClick={showComposer}
          sx={{
            px: 2.5,
            background: "linear-gradient(135deg, #4f5ff7, #7255ef)",
          }}
        >
          Nouveau message
        </Button>
      </Box>

      <Tabs
        value={folder}
        onChange={(_, value: Folder | "compose") => {
          if (value === "compose") {
            showComposer();
            return;
          }
          setFolder(value);
          setSelectedId(null);
          setReplyTo(null);
        }}
        variant="scrollable"
        scrollButtons={false}
        sx={{
          mt: 3,
          mb: 2.5,
          minHeight: 48,
          width: "fit-content",
          maxWidth: "100%",
          border: "1px solid #e1e5f2",
          borderRadius: 2.5,
          bgcolor: "#fff",
          "& .MuiTab-root": { minHeight: 48, px: { xs: 2, sm: 3 } },
        }}
      >
        <Tab
          value="inbox"
          icon={
            <Badge
              color="primary"
              badgeContent={unread.data ?? 0}
              max={99}
            >
              <InboxRoundedIcon fontSize="small" />
            </Badge>
          }
          iconPosition="start"
          label="Messages reçus"
        />
        <Tab
          value="sent"
          icon={<OutboxRoundedIcon fontSize="small" />}
          iconPosition="start"
          label="Messages envoyés"
        />
        <Tab
          value="compose"
          icon={<AddRoundedIcon fontSize="small" />}
          iconPosition="start"
          label="Nouveau message"
        />
      </Tabs>

      {currentQuery.isError && (
        <Alert severity="error" sx={{ mb: 2 }}>
          Impossible de charger les messages. Vérifiez votre connexion puis
          réessayez.
        </Alert>
      )}

      <Box
        sx={{
          display: "grid",
          gridTemplateColumns: {
            xs: "minmax(0, 1fr)",
            md: "330px minmax(0, 1fr)",
            lg: "300px minmax(360px, 1fr) 330px",
            xl: "350px minmax(420px, 1fr) 370px",
          },
          gap: 2.5,
          alignItems: "start",
        }}
      >
        <MessageList
          messages={messages}
          selectedId={effectiveSelectedId}
          folder={folder}
          loading={currentQuery.isLoading}
          onSelect={setSelectedId}
        />

        <MessageDetail
          message={detail.data}
          folder={folder}
          loading={detail.isLoading}
          onReply={startReply}
          onMarkAsRead={(messageId) => markAsRead.mutate(messageId)}
        />

        <Box
          ref={composerRef}
          sx={{
            gridColumn: { xs: "1", md: "1 / -1", lg: "auto" },
            scrollMarginTop: 92,
          }}
        >
          <MessageComposer
            replyTo={replyTo}
            onCancelReply={() => setReplyTo(null)}
            onSent={() => setFolder("sent")}
          />
        </Box>
      </Box>
    </Box>
  );
}
