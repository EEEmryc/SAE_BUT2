import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  accountRequestsApi,
  type AccountRequestPayload,
  type AccountRequestStatus,
} from "../services/accountRequestsApi";

export const accountRequestKeys = {
  all: ["account-requests"] as const,
  lists: ["account-requests", "list"] as const,
  list: (status?: AccountRequestStatus) =>
    ["account-requests", "list", status ?? "ALL"] as const,
  detail: (id: number) => ["account-requests", "detail", id] as const,
};

export function useSubmitAccountRequest() {
  return useMutation({
    mutationFn: (payload: AccountRequestPayload) =>
      accountRequestsApi.submit(payload),
  });
}

export function useAccountRequests(status?: AccountRequestStatus) {
  return useQuery({
    queryKey: accountRequestKeys.list(status),
    queryFn: () => accountRequestsApi.list(status),
  });
}

export function usePendingAccountRequests() {
  return useQuery({
    queryKey: accountRequestKeys.list("EN_ATTENTE"),
    queryFn: () => accountRequestsApi.list("EN_ATTENTE"),
    refetchInterval: 30_000,
    refetchOnMount: "always",
  });
}

export function useAccountRequest(id: number) {
  return useQuery({
    queryKey: accountRequestKeys.detail(id),
    queryFn: () => accountRequestsApi.get(id),
    enabled: Number.isFinite(id),
  });
}

export function useDecideAccountRequest() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      id,
      statut,
    }: {
      id: number;
      statut: "ACCEPTEE" | "REFUSEE";
    }) => accountRequestsApi.decide(id, statut),
    onSuccess: (request) => {
      queryClient.setQueryData(
        accountRequestKeys.detail(request.id),
        request,
      );
      void queryClient.invalidateQueries({
        queryKey: accountRequestKeys.lists,
      });
    },
  });
}
