import { httpClient } from "../../../http/httpClient";
import type {
  Chapter,
  Course,
  CourseResource,
  Enrollment,
} from "../../courses/services/coursesApi";

export type CatalogCourse = {
  id: number;
  titre: string;
  description: string;
  statut: string;
  profNom: string | null;
  profPrenom: string | null;
  profEmail: string | null;
  nombreChapitres: number;
  nombreRessources: number;
  statutInscription: "EN_ATTENTE" | "VALIDE" | "REFUSE" | null;
};

export type ChapterProgress = {
  id: number;
  statut: "NON_COMMENCE" | "EN_COURS" | "TERMINE";
  pourcentage: number;
  dateDebut: string | null;
  dateMiseAJour: string | null;
  dateFin: string | null;
  chapitreId: number | null;
  chapitreTitre: string | null;
};

export type CourseProgress = {
  coursId: number;
  coursTitre: string;
  profNom: string | null;
  profPrenom: string | null;
  totalChapitres: number;
  chapitresTermines: number;
  totalRessources: number;
  pourcentageGlobal: number;
  details: ChapterProgress[];
};

export const studentLearningApi = {
  async catalogue() {
    const response = await httpClient.get<CatalogCourse[]>("/api/cours/catalogue");
    return response.data;
  },

  async enroll(courseId: number) {
    const response = await httpClient.post<Enrollment>(
      `/api/inscriptions/cours/${courseId}`,
    );
    return response.data;
  },

  async getCourse(courseId: number) {
    const response = await httpClient.get<Course>(`/api/cours/${courseId}`);
    return response.data;
  },

  async getChapters(courseId: number) {
    const response = await httpClient.get<Chapter[]>(
      `/api/cours/${courseId}/chapitres`,
    );
    return response.data;
  },

  async getResources(courseId: number) {
    const response = await httpClient.get<CourseResource[]>(
      `/api/cours/${courseId}/ressources`,
    );
    return response.data;
  },

  async getCourseProgress(courseId: number) {
    const response = await httpClient.get<CourseProgress>(
      `/api/progressions/cours/${courseId}`,
    );
    return response.data;
  },

  async getAllProgress() {
    const response = await httpClient.get<CourseProgress[]>("/api/progressions");
    return response.data;
  },

  async startChapter(chapterId: number) {
    const response = await httpClient.post<ChapterProgress>(
      `/api/progressions/chapitres/${chapterId}/commencer`,
    );
    return response.data;
  },

  async completeChapter(chapterId: number) {
    const response = await httpClient.post<ChapterProgress>(
      `/api/progressions/chapitres/${chapterId}/terminer`,
    );
    return response.data;
  },

  async download(url: string, fileName: string) {
    const response = await httpClient.get<Blob>(url, { responseType: "blob" });
    const objectUrl = URL.createObjectURL(response.data);
    const anchor = document.createElement("a");
    anchor.href = objectUrl;
    anchor.download = fileName;
    anchor.click();
    URL.revokeObjectURL(objectUrl);
  },
};
