import {
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query";
import {
  messagingApi,
  type Message,
  type SendMessagePayload,
} from "../api/messagingApi";

export const messagingKeys = {
  all: ["messaging"] as const,
  inbox: ["messaging", "inbox"] as const,
  outbox: ["messaging", "outbox"] as const,
  detail: (id: number) => ["messaging", "detail", id] as const,
  recipients: ["messaging", "recipients"] as const,
  unread: ["messaging", "unread"] as const,
};

export function useInbox() {
  return useQuery({
    queryKey: messagingKeys.inbox,
    queryFn: messagingApi.getInbox,
  });
}

export function useOutbox() {
  return useQuery({
    queryKey: messagingKeys.outbox,
    queryFn: messagingApi.getOutbox,
  });
}

export function useMessageDetail(messageId: number | null) {
  const queryClient = useQueryClient();

  return useQuery({
    queryKey: messagingKeys.detail(messageId ?? 0),
    enabled: messageId !== null,
    queryFn: async () => {
      const message = await messagingApi.getById(messageId as number);

      queryClient.setQueryData<Message[]>(messagingKeys.inbox, (messages) =>
        messages?.map((item) =>
          item.id === message.id ? { ...item, ...message } : item,
        ),
      );
      void queryClient.invalidateQueries({ queryKey: messagingKeys.unread });
      return message;
    },
  });
}

export function useRecipients() {
  return useQuery({
    queryKey: messagingKeys.recipients,
    queryFn: messagingApi.getRecipients,
    staleTime: 60_000,
  });
}

export function useUnreadCount() {
  return useQuery({
    queryKey: messagingKeys.unread,
    queryFn: messagingApi.getUnreadCount,
    refetchInterval: 60_000,
  });
}

export function useSendMessage() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: SendMessagePayload) => messagingApi.send(payload),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: messagingKeys.outbox });
    },
  });
}

export function useReplyToMessage() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      messageId,
      contenu,
    }: {
      messageId: number;
      contenu: string;
    }) => messagingApi.reply(messageId, contenu),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: messagingKeys.inbox });
      void queryClient.invalidateQueries({ queryKey: messagingKeys.outbox });
      void queryClient.invalidateQueries({ queryKey: messagingKeys.unread });
    },
  });
}

export function useMarkMessageAsRead() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (messageId: number) => messagingApi.markAsRead(messageId),
    onSuccess: (message) => {
      queryClient.setQueryData<Message[]>(messagingKeys.inbox, (messages) =>
        messages?.map((item) =>
          item.id === message.id ? { ...item, ...message } : item,
        ),
      );
      queryClient.setQueryData(messagingKeys.detail(message.id), message);
      void queryClient.invalidateQueries({ queryKey: messagingKeys.unread });
    },
  });
}
