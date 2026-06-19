import { useQuery } from "@tanstack/react-query";
import { progressionApi } from "../services/progressionApi";

export function useProfessorProgress() {
  return useQuery({
    queryKey: ["progressions", "professor", "students"],
    queryFn: progressionApi.getProfessorStudents,
  });
}
