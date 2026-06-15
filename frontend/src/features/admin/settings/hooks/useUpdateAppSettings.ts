import { useMutation, useQueryClient } from "@tanstack/react-query";
import {
  adminSettingsApi,
  type UpdateAppSettingsPayload,
} from "../services/adminSettingsApi";

export function useUpdateAppSettings() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: UpdateAppSettingsPayload) =>
      adminSettingsApi.updateSettings(payload),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ["admin", "settings"] });
    },
  });
}
