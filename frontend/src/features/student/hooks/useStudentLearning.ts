import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { studentLearningApi } from "../services/studentLearningApi";

export const studentLearningKeys = {
  catalogue: ["student", "catalogue"] as const,
  course: (courseId: number) => ["student", "course", courseId] as const,
  chapters: (courseId: number) => ["student", "course", courseId, "chapters"] as const,
  resources: (courseId: number) => ["student", "course", courseId, "resources"] as const,
  progress: (courseId: number) => ["student", "course", courseId, "progress"] as const,
  allProgress: ["student", "progress"] as const,
};

export function useStudentCatalogue() {
  return useQuery({
    queryKey: studentLearningKeys.catalogue,
    queryFn: studentLearningApi.catalogue,
  });
}

export function useEnrollInCourse() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: studentLearningApi.enroll,
    onSuccess: () =>
      queryClient.invalidateQueries({ queryKey: studentLearningKeys.catalogue }),
  });
}

export function useStudentCourse(courseId: number) {
  return useQuery({
    queryKey: studentLearningKeys.course(courseId),
    queryFn: () => studentLearningApi.getCourse(courseId),
    enabled: Number.isFinite(courseId),
  });
}

export function useStudentChapters(courseId: number) {
  return useQuery({
    queryKey: studentLearningKeys.chapters(courseId),
    queryFn: () => studentLearningApi.getChapters(courseId),
    enabled: Number.isFinite(courseId),
  });
}

export function useStudentResources(courseId: number) {
  return useQuery({
    queryKey: studentLearningKeys.resources(courseId),
    queryFn: () => studentLearningApi.getResources(courseId),
    enabled: Number.isFinite(courseId),
  });
}

export function useCourseProgress(courseId: number) {
  return useQuery({
    queryKey: studentLearningKeys.progress(courseId),
    queryFn: () => studentLearningApi.getCourseProgress(courseId),
    enabled: Number.isFinite(courseId),
  });
}

export function useAllStudentProgress() {
  return useQuery({
    queryKey: studentLearningKeys.allProgress,
    queryFn: studentLearningApi.getAllProgress,
  });
}

export function useStartChapter(courseId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: studentLearningApi.startChapter,
    onSuccess: () =>
      queryClient.invalidateQueries({
        queryKey: studentLearningKeys.progress(courseId),
      }),
  });
}

export function useCompleteChapter(courseId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: studentLearningApi.completeChapter,
    onSuccess: () =>
      Promise.all([
        queryClient.invalidateQueries({
          queryKey: studentLearningKeys.progress(courseId),
        }),
        queryClient.invalidateQueries({
          queryKey: studentLearningKeys.allProgress,
        }),
      ]),
  });
}
