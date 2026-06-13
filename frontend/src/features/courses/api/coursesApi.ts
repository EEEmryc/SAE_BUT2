import { httpClient } from "../../../api/httpClient";
import type { UserRole } from "../../auth/api/authApi";

export type CourseStatus = "DRAFT" | "PUBLISHED" | "VALIDE" | "ARCHIVE";

export type Course = {
  id: number;
  titre: string;
  description: string;
  dateCreation: string;
  statut: CourseStatus;
  visibleCatalogue: boolean;
  profNom: string;
  profPrenom: string;
  profEmail: string;
};

export type CoursePayload = {
  titre: string;
  description: string;
  statut: CourseStatus;
  visibleCatalogue: boolean;
};

export type Chapter = {
  id: number;
  titre: string;
  contenu: string;
  ordre: number;
  dateCreation: string;
  coursId: number;
  coursTitre: string;
};

export type ChapterPayload = {
  titre: string;
  contenu: string;
  ordre: number;
};

export type CourseResource = {
  id: number;
  nom: string;
  url: string;
  type: string;
  telechargeable: boolean;
  dateCreation: string;
  chapitreId: number;
  chapitreTitre: string;
};

export type ResourcePayload = {
  nom: string;
  url: string;
  type: string;
  telechargeable: boolean;
};

export type Enrollment = {
  id: number;
  statut: "EN_ATTENTE" | "VALIDE" | "REFUSE";
  dateInscription: string;
  coursId: number;
  coursTitre: string;
  eleveId: number;
  eleveNom: string;
  elevePrenom: string;
  eleveEmail: string;
};

export type Student = {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: UserRole;
  statut: string;
  dateCreation: string | null;
};

export const coursesApi = {
  async list() {
    const response = await httpClient.get<Course[]>("/api/cours");
    return response.data;
  },

  async get(id: number) {
    const response = await httpClient.get<Course>(`/api/cours/${id}`);
    return response.data;
  },

  async create(payload: CoursePayload) {
    const response = await httpClient.post<Course>("/api/cours", payload);
    return response.data;
  },

  async update(id: number, payload: CoursePayload) {
    const response = await httpClient.put<Course>(`/api/cours/${id}`, payload);
    return response.data;
  },

  async listChapters(courseId: number) {
    const response = await httpClient.get<Chapter[]>(
      `/api/cours/${courseId}/chapitres`,
    );
    return response.data;
  },

  async createChapter(courseId: number, payload: ChapterPayload) {
    const response = await httpClient.post<Chapter>(
      `/api/cours/${courseId}/chapitres`,
      payload,
    );
    return response.data;
  },

  async updateChapter(
    courseId: number,
    chapterId: number,
    payload: ChapterPayload,
  ) {
    const response = await httpClient.put<Chapter>(
      `/api/cours/${courseId}/chapitres/${chapterId}`,
      payload,
    );
    return response.data;
  },

  async listResources(courseId: number, chapterId: number) {
    const response = await httpClient.get<CourseResource[]>(
      `/api/cours/${courseId}/chapitres/${chapterId}/ressources`,
    );
    return response.data;
  },

  async createResource(
    courseId: number,
    chapterId: number,
    payload: ResourcePayload,
  ) {
    const response = await httpClient.post<CourseResource>(
      `/api/cours/${courseId}/chapitres/${chapterId}/ressources`,
      payload,
    );
    return response.data;
  },

  async listEnrollments(courseId: number) {
    const response = await httpClient.get<Enrollment[]>(
      `/api/inscriptions/cours/${courseId}/etudiants`,
    );
    return response.data;
  },

  async listStudents() {
    const response = await httpClient.get<Student[]>("/api/inscriptions/etudiants");
    return response.data;
  },

  async enrollStudent(courseId: number, studentId: number) {
    const response = await httpClient.post<Enrollment>(
      `/api/inscriptions/cours/${courseId}/etudiants`,
      { eleveId: studentId },
    );
    return response.data;
  },
};
