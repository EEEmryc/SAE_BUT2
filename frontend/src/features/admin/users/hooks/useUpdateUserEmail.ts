import { useMutation, useQueryClient } from "@tanstack/react-query";
import { adminUsersApi, type AdminUser } from "../services/adminUsersApi";

export function useUpdateUserEmail() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, newEmail }: { id: number; newEmail: string }) =>
      adminUsersApi.updateEmail(id, newEmail),
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
