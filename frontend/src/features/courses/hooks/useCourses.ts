import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  coursesApi,
  type ChapterPayload,
  type CoursePayload,
  type ResourcePayload,
} from "../api/coursesApi";

export const courseKeys = {
  all: ["courses"] as const,
  detail: (courseId: number) => ["courses", courseId] as const,
  chapters: (courseId: number) => ["courses", courseId, "chapters"] as const,
  resources: (courseId: number, chapterId: number) =>
    ["courses", courseId, "chapters", chapterId, "resources"] as const,
  enrollments: (courseId: number) =>
    ["courses", courseId, "enrollments"] as const,
  students: ["students"] as const,
};

export function useCourses() {
  return useQuery({
    queryKey: courseKeys.all,
    queryFn: coursesApi.list,
  });
}

export function useCourse(courseId: number) {
  return useQuery({
    queryKey: courseKeys.detail(courseId),
    queryFn: () => coursesApi.get(courseId),
    enabled: Number.isFinite(courseId),
  });
}

export function useCreateCourse() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: coursesApi.create,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: courseKeys.all }),
  });
}

export function useUpdateCourse(courseId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CoursePayload) => coursesApi.update(courseId, payload),
    onSuccess: (course) => {
      queryClient.setQueryData(courseKeys.detail(courseId), course);
      void queryClient.invalidateQueries({ queryKey: courseKeys.all });
    },
  });
}

export function useChapters(courseId: number) {
  return useQuery({
    queryKey: courseKeys.chapters(courseId),
    queryFn: () => coursesApi.listChapters(courseId),
    enabled: Number.isFinite(courseId),
  });
}

export function useSaveChapter(courseId: number, chapterId?: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: ChapterPayload) =>
      chapterId
        ? coursesApi.updateChapter(courseId, chapterId, payload)
        : coursesApi.createChapter(courseId, payload),
    onSuccess: () =>
      queryClient.invalidateQueries({ queryKey: courseKeys.chapters(courseId) }),
  });
}

export function useResources(courseId: number, chapterId: number) {
  return useQuery({
    queryKey: courseKeys.resources(courseId, chapterId),
    queryFn: () => coursesApi.listResources(courseId, chapterId),
  });
}

export function useCreateResource(courseId: number, chapterId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: ResourcePayload) =>
      coursesApi.createResource(courseId, chapterId, payload),
    onSuccess: () =>
      queryClient.invalidateQueries({
        queryKey: courseKeys.resources(courseId, chapterId),
      }),
  });
}

export function useEnrollments(courseId: number) {
  return useQuery({
    queryKey: courseKeys.enrollments(courseId),
    queryFn: () => coursesApi.listEnrollments(courseId),
    enabled: Number.isFinite(courseId),
  });
}

export function useStudents() {
  return useQuery({
    queryKey: courseKeys.students,
    queryFn: coursesApi.listStudents,
  });
}

export function useEnrollStudent(courseId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (studentId: number) =>
      coursesApi.enrollStudent(courseId, studentId),
    onSuccess: () =>
      queryClient.invalidateQueries({
        queryKey: courseKeys.enrollments(courseId),
      }),
  });
}
