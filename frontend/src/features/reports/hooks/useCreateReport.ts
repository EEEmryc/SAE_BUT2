import { useMutation, useQueryClient } from "@tanstack/react-query";
import { reportsApi, type CreateReportPayload } from "../api/reportsApi";
import { reportsKeys } from "./useReports";

export function useCreateReport() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: CreateReportPayload) => reportsApi.create(payload),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: reportsKeys.all });
    },
  });
}