import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  coursesApi,
  type ChapterPayload,
  type CoursePayload,
} from "../api/coursesApi";

export const courseKeys = {
  all: ["courses"] as const,
  detail: (courseId: number) => ["courses", courseId] as const,
  summary: (courseId: number) => ["courses", courseId, "summary"] as const,
  chapters: (courseId: number) => ["courses", courseId, "chapters"] as const,
  resources: (courseId: number, chapterId: number) =>
    ["courses", courseId, "chapters", chapterId, "resources"] as const,
  courseResources: (courseId: number) =>
    ["courses", courseId, "resources"] as const,
  enrollments: (courseId: number) =>
    ["courses", courseId, "enrollments"] as const,
  pendingEnrollments: (professorEmail: string) =>
    ["courses", "pending-enrollments", professorEmail] as const,
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

export function useDeleteCourse() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: coursesApi.delete,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: courseKeys.all }),
  });
}

export function useUploadCourseMainFile() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ courseId, file }: { courseId: number; file: File }) =>
      coursesApi.uploadMainFile(courseId, file),
    onSuccess: (course) => {
      queryClient.setQueryData(courseKeys.detail(course.id), course);
      void queryClient.invalidateQueries({ queryKey: courseKeys.all });
    },
  });
}

export function useDeleteCourseMainFile(courseId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => coursesApi.deleteMainFile(courseId),
    onSuccess: (course) => {
      queryClient.setQueryData(courseKeys.detail(courseId), course);
      void queryClient.invalidateQueries({ queryKey: courseKeys.all });
    },
  });
}

export function useCourseSummary(courseId: number) {
  return useQuery({
    queryKey: courseKeys.summary(courseId),
    queryFn: () => coursesApi.getSummary(courseId),
    enabled: Number.isFinite(courseId),
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
      Promise.all([
        queryClient.invalidateQueries({
          queryKey: courseKeys.chapters(courseId),
        }),
        queryClient.invalidateQueries({
          queryKey: courseKeys.summary(courseId),
        }),
      ]),
  });
}

export function useDeleteChapter(courseId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (chapterId: number) =>
      coursesApi.deleteChapter(courseId, chapterId),
    onSuccess: () => {
      void queryClient.invalidateQueries({
        queryKey: courseKeys.chapters(courseId),
      });
      void queryClient.invalidateQueries({
        queryKey: courseKeys.courseResources(courseId),
      });
      void queryClient.invalidateQueries({
        queryKey: courseKeys.summary(courseId),
      });
    },
  });
}

export function useUploadChapterMainFile(courseId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ chapterId, file }: { chapterId: number; file: File }) =>
      coursesApi.uploadChapterMainFile(courseId, chapterId, file),
    onSuccess: () =>
      queryClient.invalidateQueries({
        queryKey: courseKeys.chapters(courseId),
      }),
  });
}

export function useDeleteChapterMainFile(courseId: number, chapterId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => coursesApi.deleteChapterMainFile(courseId, chapterId),
    onSuccess: () =>
      queryClient.invalidateQueries({
        queryKey: courseKeys.chapters(courseId),
      }),
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
    mutationFn: (payload: {
      file: File;
      nom: string;
      telechargeable: boolean;
    }) =>
      coursesApi.uploadResource(
        courseId,
        chapterId,
        payload.file,
        payload.nom,
        payload.telechargeable,
      ),
    onSuccess: () =>
      Promise.all([
        queryClient.invalidateQueries({
          queryKey: courseKeys.resources(courseId, chapterId),
        }),
        queryClient.invalidateQueries({
          queryKey: courseKeys.courseResources(courseId),
        }),
        queryClient.invalidateQueries({
          queryKey: courseKeys.summary(courseId),
        }),
      ]),
  });
}

export function useCourseResources(courseId: number) {
  return useQuery({
    queryKey: courseKeys.courseResources(courseId),
    queryFn: () => coursesApi.listCourseResources(courseId),
    enabled: Number.isFinite(courseId),
  });
}

export function useDeleteResource(courseId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      chapterId,
      resourceId,
    }: {
      chapterId: number;
      resourceId: number;
    }) => coursesApi.deleteResource(courseId, chapterId, resourceId),
    onSuccess: (_, variables) => {
      void queryClient.invalidateQueries({
        queryKey: courseKeys.resources(courseId, variables.chapterId),
      });
      void queryClient.invalidateQueries({
        queryKey: courseKeys.courseResources(courseId),
      });
      void queryClient.invalidateQueries({
        queryKey: courseKeys.summary(courseId),
      });
    },
  });
}

export function useEnrollments(courseId: number) {
  return useQuery({
    queryKey: courseKeys.enrollments(courseId),
    queryFn: () => coursesApi.listEnrollments(courseId),
    enabled: Number.isFinite(courseId),
  });
}

export function usePendingEnrollmentRequests(
  professorEmail: string,
  enabled = true,
) {
  return useQuery({
    queryKey: courseKeys.pendingEnrollments(professorEmail),
    queryFn: coursesApi.listPendingEnrollmentRequests,
    enabled: enabled && Boolean(professorEmail),
    refetchOnMount: "always",
    refetchInterval: enabled ? 30_000 : false,
  });
}

export function useUpdateEnrollmentStatus(courseId?: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      enrollmentId,
      statut,
    }: {
      enrollmentId: number;
      statut: "VALIDE" | "REFUSE";
    }) => coursesApi.updateEnrollmentStatus(enrollmentId, statut),
    onSuccess: () => {
      void queryClient.invalidateQueries({
        queryKey: ["courses", "pending-enrollments"],
      });
      if (courseId) {
        void queryClient.invalidateQueries({
          queryKey: courseKeys.enrollments(courseId),
        });
        void queryClient.invalidateQueries({
          queryKey: courseKeys.summary(courseId),
        });
      }
    },
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
      Promise.all([
        queryClient.invalidateQueries({
          queryKey: courseKeys.enrollments(courseId),
        }),
        queryClient.invalidateQueries({
          queryKey: courseKeys.summary(courseId),
        }),
      ]),
  });
}

export function useRemoveEnrollment(courseId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (enrollmentId: number) =>
      coursesApi.removeEnrollment(enrollmentId),
    onSuccess: () =>
      Promise.all([
        queryClient.invalidateQueries({
          queryKey: courseKeys.enrollments(courseId),
        }),
        queryClient.invalidateQueries({
          queryKey: courseKeys.summary(courseId),
        }),
      ]),
  });
}
