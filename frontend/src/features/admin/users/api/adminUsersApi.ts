import { httpClient } from "../../../../api/httpClient";
import type {
  UserProfile,
  UserRole,
} from "../../../auth/api/authApi";

export type UserStatus = "ACTIF" | "INACTIF";

export type AdminUser = UserProfile & {
  statut: UserStatus;
  dateCreation: string | null;
};

export type CreateUserPayload = {
  nom: string;
  prenom: string;
  email: string;
  password: string;
  role: UserRole;
  statut: UserStatus;
};

export type UserCreationResponse = {
  user: AdminUser;
  invitationEmailSent: boolean;
};

export const adminUsersApi = {
  async list() {
    const response = await httpClient.get<AdminUser[]>("/api/admin/users");
    return response.data;
  },

  async create(payload: CreateUserPayload) {
    const response = await httpClient.post<UserCreationResponse>(
      "/api/admin/users",
      payload,
    );
    return response.data;
  },
};
