import { useMutation, useQueryClient } from "@tanstack/react-query";
import {
  adminUsersApi,
  type CreateUserPayload,
} from "../api/adminUsersApi";

export function useCreateUser() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: CreateUserPayload) =>
      adminUsersApi.create(payload),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ["admin", "users"] });
    },
  });
}
