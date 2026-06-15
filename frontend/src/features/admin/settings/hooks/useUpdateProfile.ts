import { useMutation } from "@tanstack/react-query";
import { useAuthStore } from "../../../../store/authStore";
import { authApi } from "../../../auth/services/authApi";

export function useUpdateProfile() {
  const setUser = useAuthStore((state) => state.setUser);

  return useMutation({
    mutationFn: (payload: {
      nom: string;
      prenom: string;
      password?: string;
    }) => authApi.updateProfile(payload),
    onSuccess: (user) => {
      setUser(user);
    },
  });
}
