import { httpClient } from "../../../http/httpClient";
import type { UserRole } from "../../auth/services/authApi";

export type AccountRequestStatus = "EN_ATTENTE" | "ACCEPTEE" | "REFUSEE";
export type RequestedRole = Extract<UserRole, "ETUDIANT" | "PROFESSEUR">;

export type AccountRequestPayload = {
  nom: string;
  prenom: string;
  email: string;
  formation: string;
  requestedRole: RequestedRole;
  commentaire: string;
};

export type AccountRequest = AccountRequestPayload & {
  id: number;
  statut: AccountRequestStatus;
  dateCreation: string;
  dateTraitement: string | null;
  confirmationEmailSent: boolean;
};

export const accountRequestsApi = {
  async submit(payload: AccountRequestPayload) {
    const response = await httpClient.post<AccountRequest>(
      "/api/account-requests",
      payload,
    );
    return response.data;
  },

  async list(status?: AccountRequestStatus) {
    const response = await httpClient.get<AccountRequest[]>(
      "/api/admin/account-requests",
      { params: status ? { status } : undefined },
    );
    return response.data;
  },

  async get(id: number) {
    const response = await httpClient.get<AccountRequest>(
      `/api/admin/account-requests/${id}`,
    );
    return response.data;
  },

  async decide(id: number, statut: Extract<AccountRequestStatus, "ACCEPTEE" | "REFUSEE">) {
    const response = await httpClient.patch<AccountRequest>(
      `/api/admin/account-requests/${id}/status`,
      { statut },
    );
    return response.data;
  },
};
