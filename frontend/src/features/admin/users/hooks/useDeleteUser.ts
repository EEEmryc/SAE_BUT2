import { useMutation, useQueryClient } from "@tanstack/react-query";
import { adminUsersApi, type AdminUser } from "../services/adminUsersApi";

export function useDeleteUser() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => adminUsersApi.deleteUser(id),
    onSuccess: (_, deletedUserId) => {
      queryClient.setQueryData<AdminUser[]>(
        ["admin", "users"],
        (users) => users?.filter((user) => user.id !== deletedUserId),
      );
    },
  });
}
