import { httpClient } from "../../../api/httpClient";
import type { UserRole } from "../../auth/api/authApi";

export type ReportStatus = "NOUVEAU" | "EN_COURS" | "TRAITE" | "RESOLU";

export type ReportCategory =
  | "CONTENU"
  | "ACCES"
  | "COMPORTEMENT"
  | "EVALUATION"
  | "TECHNIQUE"
  | "COMPTE"
  | "MESSAGERIE"
  | "AUTRE";

export type Report = {
  id: number;
  sujet: string;
  description: string;
  categorie: ReportCategory;
  statut: ReportStatus;
  dateEnvoi: string;
  pieceJointeNom: string | null;
  pieceJointeUrl: string | null;
  auteurId: number;
  auteurNom: string;
  auteurPrenom: string;
  auteurEmail: string;
  auteurRole: Exclude<UserRole, "ADMIN">;
};

export type CreateReportPayload = {
  sujet: string;
  description: string;
  categorie: ReportCategory;
  pieceJointeNom?: string | null;
  pieceJointeUrl?: string | null;
};

export const reportsApi = {
  async create(payload: CreateReportPayload) {
    const response = await httpClient.post<Report>(
      "/api/signalements",
      payload,
    );
    return response.data;
  },

  async list() {
    const response = await httpClient.get<Report[]>("/api/signalements");
    return response.data;
  },

  async getById(id: number) {
    const response = await httpClient.get<Report>(`/api/signalements/${id}`);
    return response.data;
  },

  async updateStatus(id: number, statut: ReportStatus) {
    const response = await httpClient.patch<Report>(
      `/api/signalements/${id}/statut`,
      { statut },
    );
    return response.data;
  },
};