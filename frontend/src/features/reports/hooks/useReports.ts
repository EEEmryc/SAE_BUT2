import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  reportsApi,
  type Report,
  type ReportStatus,
} from "../services/reportsApi";

export const reportsKeys = {
  all: ["reports"] as const,
  detail: (id: number) => ["reports", "detail", id] as const,
};

export function useReports() {
  return useQuery({
    queryKey: reportsKeys.all,
    queryFn: reportsApi.list,
  });
}

export function useReportDetail(reportId: number | null) {
  return useQuery({
    queryKey: reportsKeys.detail(reportId ?? 0),
    queryFn: () => reportsApi.getById(reportId as number),
    enabled: reportId !== null,
  });
}

export function useUpdateReportStatus() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      reportId,
      statut,
    }: {
      reportId: number;
      statut: ReportStatus;
    }) => reportsApi.updateStatus(reportId, statut),
    onSuccess: (updatedReport) => {
      queryClient.setQueryData<Report[]>(reportsKeys.all, (reports) =>
        reports?.map((report) =>
          report.id === updatedReport.id ? updatedReport : report,
        ),
      );
      queryClient.setQueryData(
        reportsKeys.detail(updatedReport.id),
        updatedReport,
      );
    },
  });
}
