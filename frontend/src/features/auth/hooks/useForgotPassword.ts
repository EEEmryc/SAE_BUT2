import { useMutation } from "@tanstack/react-query";
import { authApi } from "../api/authApi";

export function useForgotPassword() {
  return useMutation({
    mutationFn: (email: string) => authApi.forgotPassword(email),
  });
}
