import { httpClient } from "../../../api/httpClient";
import type { UserRole } from "../../auth/api/authApi";

export type Message = {
  id: number;
  sujet: string;
  contenu: string;
  dateEnvoi: string;
  lu: boolean;
  dateLecture: string | null;
  expediteurId: number;
  expediteurNom: string;
  expediteurPrenom: string;
  expediteurEmail: string;
  destinataireId: number;
  destinataireNom: string;
  destinatairePrenom: string;
  destinataireEmail: string;
};

export type MessageRecipient = {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: UserRole;
};

export type SendMessagePayload = {
  emailDestinataire: string;
  sujet: string;
  contenu: string;
};

export const messagingApi = {
  async getInbox() {
    const response = await httpClient.get<Message[]>("/api/messages/recus");
    return response.data;
  },

  async getOutbox() {
    const response = await httpClient.get<Message[]>("/api/messages/envoyes");
    return response.data;
  },

  async getById(id: number) {
    const response = await httpClient.get<Message>(`/api/messages/${id}`);
    return response.data;
  },

  async getRecipients() {
    const response = await httpClient.get<MessageRecipient[]>(
      "/api/messages/destinataires",
    );
    return response.data;
  },

  async getUnreadCount() {
    const response = await httpClient.get<{ nonLus: number }>(
      "/api/messages/non-lus",
    );
    return response.data.nonLus;
  },

  async send(payload: SendMessagePayload) {
    const response = await httpClient.post<Message>("/api/messages", payload);
    return response.data;
  },

  async reply(messageId: number, contenu: string) {
    const response = await httpClient.post<Message>(
      `/api/messages/${messageId}/repondre`,
      { contenu },
    );
    return response.data;
  },

  async markAsRead(messageId: number) {
    const response = await httpClient.patch<Message>(
      `/api/messages/${messageId}/lu`,
    );
    return response.data;
  },
};
