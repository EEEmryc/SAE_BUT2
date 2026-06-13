import { useMutation } from "@tanstack/react-query";
import {
  adminUsersApi,
  type CreateUserPayload,
} from "../api/adminUsersApi";

export function useCreateUser() {
  return useMutation({
    mutationFn: (payload: CreateUserPayload) =>
      adminUsersApi.create(payload),
  });
}
