import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../../../store/authStore";
import { authApi, type LoginPayload } from "../api/authApi";

export function useLogin() {
  const navigate = useNavigate();
  const setSession = useAuthStore((state) => state.setSession);

  return useMutation({
    mutationFn: (payload: LoginPayload) => authApi.login(payload),
    onSuccess: ({ token, refreshToken }) => {
      setSession(token, refreshToken);
      navigate("/dashboard", { replace: true });
    },
  });
}
