import { useMutation } from "@tanstack/react-query";
import { authApi } from "../services/authApi";

type ResetPasswordPayload = {
  token: string;
  password: string;
};

export function useResetPassword() {
  return useMutation({
    mutationFn: ({ token, password }: ResetPasswordPayload) =>
      authApi.resetPassword(token, password),
  });
}
