import { useQuery } from "@tanstack/react-query";
import { adminSettingsApi } from "../services/adminSettingsApi";

export function useAppSettings() {
  return useQuery({
    queryKey: ["admin", "settings"],
    queryFn: adminSettingsApi.getSettings,
  });
}
