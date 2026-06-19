import { useMutation, useQueryClient } from "@tanstack/react-query";
import { adminUsersApi, type AdminUser } from "../services/adminUsersApi";

export function useToggleUserStatus() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => adminUsersApi.toggleStatus(id),
    onSuccess: (updatedUser) => {
      queryClient.setQueryData<AdminUser[]>(
        ["admin", "users"],
        (users) =>
          users?.map((user) =>
            user.id === updatedUser.id ? updatedUser : user,
          ),
      );
    },
  });
}
