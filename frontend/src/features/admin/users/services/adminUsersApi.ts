import axios from "axios";
import { httpClient } from "../../../../http/httpClient";
import type {
  UserProfile,
  UserRole,
} from "../../../auth/services/authApi";

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

type AdminUserResponse = Omit<AdminUser, "statut"> & {
  statut: string;
};

export function normalizeUserStatus(status: string): UserStatus {
  return status.trim().toUpperCase() === "INACTIF" ? "INACTIF" : "ACTIF";
}

function normalizeAdminUser(user: AdminUserResponse): AdminUser {
  return {
    ...user,
    statut: normalizeUserStatus(user.statut),
  };
}

function isNotFound(error: unknown) {
  return axios.isAxiosError(error) && error.response?.status === 404;
}

async function deleteUserAtPath(path: string) {
  const response = await httpClient.delete<AdminUserResponse | undefined>(path);
  return response.data ? normalizeAdminUser(response.data) : undefined;
}

export const adminUsersApi = {
  async list() {
    const response =
      await httpClient.get<AdminUserResponse[]>("/api/admin/users");
    return response.data.map(normalizeAdminUser);
  },

  async create(payload: CreateUserPayload) {
    const response = await httpClient.post<UserCreationResponse>(
      "/api/admin/users",
      payload,
    );
    return response.data;
  },

  async deactivate(id: number) {
    try {
      return await deleteUserAtPath(`/api/admin/users/${id}`);
    } catch (error) {
      if (!isNotFound(error)) {
        throw error;
      }

      return deleteUserAtPath(`/api/admin/${id}`);
    }
  },
};
