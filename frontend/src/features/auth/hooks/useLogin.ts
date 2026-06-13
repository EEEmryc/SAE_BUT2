import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../../../store/authStore";
import { authApi, type LoginPayload } from "../api/authApi";

export function useLogin() {
  const navigate = useNavigate();
  const establishSession = useAuthStore((state) => state.establishSession);

  return useMutation({
    mutationFn: async (payload: LoginPayload) => {
      const session = await authApi.login(payload);
      await establishSession(session.token, session.refreshToken);
      return session;
    },
    onSuccess: () => {
      navigate("/dashboard", { replace: true });
    },
  });
}
