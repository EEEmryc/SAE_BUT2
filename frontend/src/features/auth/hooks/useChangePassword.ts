import { useMutation } from "@tanstack/react-query";
import { authApi } from "../services/authApi";

type ChangePasswordPayload = {
  currentPassword: string;
  newPassword: string;
};

export function useChangePassword() {
  return useMutation({
    mutationFn: ({ currentPassword, newPassword }: ChangePasswordPayload) =>
      authApi.changePassword(currentPassword, newPassword),
  });
}