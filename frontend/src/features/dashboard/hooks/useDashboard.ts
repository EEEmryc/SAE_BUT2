import { useQuery } from "@tanstack/react-query";
import { dashboardApi } from "../services/dashboardApi";

export const dashboardKeys = {
  student: ["dashboard", "student"] as const,
  professor: ["dashboard", "professor"] as const,
  admin: ["dashboard", "admin"] as const,
};

export function useStudentDashboard() {
  return useQuery({
    queryKey: dashboardKeys.student,
    queryFn: dashboardApi.student,
  });
}

export function useProfessorDashboard() {
  return useQuery({
    queryKey: dashboardKeys.professor,
    queryFn: dashboardApi.professor,
  });
}

export function useAdminDashboard() {
  return useQuery({
    queryKey: dashboardKeys.admin,
    queryFn: dashboardApi.admin,
  });
}
