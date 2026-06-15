import { useQuery } from "@tanstack/react-query";
import { adminUsersApi } from "../services/adminUsersApi";

export function useAdminUsers() {
  return useQuery({
    queryKey: ["admin", "users"],
    queryFn: adminUsersApi.list,
  });
}
