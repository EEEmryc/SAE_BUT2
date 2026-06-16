import { useMutation, useQueryClient } from "@tanstack/react-query";
import { adminUsersApi } from "../services/adminUsersApi";

export function useDeactivateUser() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => adminUsersApi.deactivate(id),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ["admin", "users"] });
    },
  });
}
