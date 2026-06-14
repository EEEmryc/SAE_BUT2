import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  reportsApi,
  type Report,
  type ReportStatus,
} from "../api/reportsApi";

export const reportsKeys = {
  all: ["reports"] as const,
  mine: ["reports", "mine"] as const,
  detail: (id: number) => ["reports", "detail", id] as const,
};

export function useReports() {
  return useQuery({
    queryKey: reportsKeys.all,
    queryFn: reportsApi.list,
  });
}

export function useMyReports() {
  return useQuery({
    queryKey: reportsKeys.mine,
    queryFn: reportsApi.listMine,
  });
}

export function useNewReportsCount() {
  return useQuery({
    queryKey: [...reportsKeys.all, "new-count"],
    queryFn: reportsApi.list,
    select: (reports) =>
      reports.filter((report) => report.statut === "NOUVEAU").length,
    refetchInterval: 60_000,
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