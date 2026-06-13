import { useQuery } from "@tanstack/react-query";
import { adminUsersApi } from "../api/adminUsersApi";

export function useAdminUsers() {
  return useQuery({
    queryKey: ["admin", "users"],
    queryFn: adminUsersApi.list,
  });
}
