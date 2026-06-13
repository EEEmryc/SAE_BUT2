import { httpClient } from "../../../../api/httpClient";
import type {
  UserProfile,
  UserRole,
} from "../../../auth/api/authApi";

export type UserStatus = "ACTIF" | "INACTIF";

export type CreateUserPayload = {
  nom: string;
  prenom: string;
  email: string;
  password: string;
  role: UserRole;
  statut: UserStatus;
};

export type UserCreationResponse = {
  user: UserProfile;
  invitationEmailSent: boolean;
};

export const adminUsersApi = {
  async create(payload: CreateUserPayload) {
    const response = await httpClient.post<UserCreationResponse>(
      "/api/admin/users",
      payload,
    );
    return response.data;
  },
};
