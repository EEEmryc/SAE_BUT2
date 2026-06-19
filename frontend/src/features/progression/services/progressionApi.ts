import { httpClient } from "../../../http/httpClient";

export type ProfessorStudentProgress = {
  inscriptionId: number;
  eleveId: number;
  eleveNom: string;
  elevePrenom: string;
  eleveEmail: string;
  coursId: number;
  coursTitre: string;
  chapitresTermines: number;
  totalChapitres: number;
  pourcentage: number;
  derniereActivite: string | null;
};

export const progressionApi = {
  async getProfessorStudents() {
    const response = await httpClient.get<ProfessorStudentProgress[]>(
      "/api/progressions/professeur/etudiants",
    );
    return response.data;
  },
};
