import { afterEach, describe, expect, it, vi } from "vitest";
import { httpClient } from "../../../api/httpClient";
import { adminUsersApi } from "../../admin/users/api/adminUsersApi";
import { coursesApi } from "../../courses/api/coursesApi";
import { messagingApi } from "../../messaging/api/messagingApi";
import { reportsApi } from "../../reports/api/reportsApi";
import { studentLearningApi } from "../../student/api/studentLearningApi";
import { dashboardApi } from "./dashboardApi";

const message = {
  id: 1,
  sujet: "Question",
  contenu: "Bonjour",
  dateEnvoi: "2026-06-14T10:00:00",
  lu: false,
  dateLecture: null,
  expediteurId: 2,
  expediteurNom: "Martin",
  expediteurPrenom: "Sophie",
  expediteurEmail: "sophie@learnhub.fr",
  destinataireId: 1,
  destinataireNom: "Admin",
  destinatairePrenom: "Yacine",
  destinataireEmail: "admin@learnhub.fr",
};

afterEach(() => {
  vi.restoreAllMocks();
});

describe("dashboardApi", () => {
  it("calcule le tableau de bord etudiant depuis sa progression", async () => {
    vi.spyOn(studentLearningApi, "getAllProgress").mockResolvedValue([
      {
        coursId: 1,
        coursTitre: "Java",
        profNom: "Dupont",
        profPrenom: "Marie",
        totalChapitres: 4,
        chapitresTermines: 3,
        totalRessources: 7,
        pourcentageGlobal: 75,
        details: [
          {
            id: 10,
            statut: "TERMINE",
            pourcentage: 100,
            dateDebut: "2026-06-13T09:00:00",
            dateMiseAJour: "2026-06-13T10:00:00",
            dateFin: "2026-06-13T10:00:00",
            chapitreId: 10,
            chapitreTitre: "Collections",
          },
        ],
      },
      {
        coursId: 2,
        coursTitre: "SQL",
        profNom: "Martin",
        profPrenom: "Paul",
        totalChapitres: 6,
        chapitresTermines: 2,
        totalRessources: 5,
        pourcentageGlobal: 33,
        details: [],
      },
    ]);
    vi.spyOn(messagingApi, "getInbox").mockResolvedValue([message]);
    vi.spyOn(messagingApi, "getOutbox").mockResolvedValue([]);

    const result = await dashboardApi.student();

    expect(result).toMatchObject({
      courses: 2,
      globalProgress: 50,
      completedChapters: 5,
      totalChapters: 10,
      availableResources: 12,
      messages: 1,
      unreadMessages: 1,
    });
    expect(result.courseMetrics[0]).toMatchObject({
      title: "Java",
      value: 75,
    });
  });

  it("agrege les cours et inscriptions du professeur", async () => {
    vi.spyOn(coursesApi, "list").mockResolvedValue([
      {
        id: 1,
        titre: "Spring Boot",
        description: "API REST",
        dateCreation: "2026-06-10T10:00:00",
        statut: "PUBLISHED",
        visibleCatalogue: true,
        fichierPrincipalNom: null,
        fichierPrincipalUrl: null,
        fichierPrincipalType: null,
        fichierPrincipalTailleOctets: null,
        profNom: "AIT HAMI",
        profPrenom: "Fadma",
        profEmail: "fadma@learnhub.fr",
      },
      {
        id: 2,
        titre: "React",
        description: "Client web",
        dateCreation: "2026-06-11T10:00:00",
        statut: "DRAFT",
        visibleCatalogue: false,
        fichierPrincipalNom: null,
        fichierPrincipalUrl: null,
        fichierPrincipalType: null,
        fichierPrincipalTailleOctets: null,
        profNom: "AIT HAMI",
        profPrenom: "Fadma",
        profEmail: "fadma@learnhub.fr",
      },
    ]);
    vi.spyOn(coursesApi, "getSummary")
      .mockResolvedValueOnce({
        students: 10,
        chapters: 4,
        resources: 8,
        averageProgress: 80,
      })
      .mockResolvedValueOnce({
        students: 5,
        chapters: 3,
        resources: 2,
        averageProgress: 20,
      });
    vi.spyOn(messagingApi, "getInbox").mockResolvedValue([message]);
    vi.spyOn(messagingApi, "getOutbox").mockResolvedValue([message]);

    const result = await dashboardApi.professor();

    expect(result).toMatchObject({
      courses: 2,
      publishedCourses: 1,
      students: 15,
      chapters: 7,
      resources: 10,
      averageProgress: 60,
      messages: 2,
      unreadMessages: 1,
    });
  });

  it("presente les statistiques globales de l administrateur", async () => {
    vi.spyOn(httpClient, "get").mockResolvedValue({
      data: { totalUsers: 4, activeCourses: 9 },
    });
    vi.spyOn(adminUsersApi, "list").mockResolvedValue([
      {
        id: 1,
        nom: "Admin",
        prenom: "Yacine",
        email: "admin@learnhub.fr",
        role: "ADMIN",
        statut: "ACTIF",
        dateCreation: "2026-06-14T08:00:00",
      },
      {
        id: 2,
        nom: "Martin",
        prenom: "Sophie",
        email: "sophie@learnhub.fr",
        role: "ETUDIANT",
        statut: "ACTIF",
        dateCreation: "2026-06-13T08:00:00",
      },
      {
        id: 3,
        nom: "Durand",
        prenom: "Lucas",
        email: "lucas@learnhub.fr",
        role: "ETUDIANT",
        statut: "INACTIF",
        dateCreation: "2026-06-12T08:00:00",
      },
      {
        id: 4,
        nom: "Dupont",
        prenom: "Marie",
        email: "marie@learnhub.fr",
        role: "PROFESSEUR",
        statut: "ACTIF",
        dateCreation: "2026-06-11T08:00:00",
      },
    ]);
    vi.spyOn(reportsApi, "list").mockResolvedValue([
      {
        id: 1,
        sujet: "Acces",
        description: "Cours inaccessible",
        categorie: "ACCES",
        statut: "NOUVEAU",
        dateEnvoi: "2026-06-14T09:00:00",
        pieceJointeNom: null,
        pieceJointeUrl: null,
        auteurId: 2,
        auteurNom: "Martin",
        auteurPrenom: "Sophie",
        auteurEmail: "sophie@learnhub.fr",
        auteurRole: "ETUDIANT",
      },
      {
        id: 2,
        sujet: "Contenu",
        description: "Contenu a verifier",
        categorie: "CONTENU",
        statut: "RESOLU",
        dateEnvoi: "2026-06-13T09:00:00",
        pieceJointeNom: null,
        pieceJointeUrl: null,
        auteurId: 4,
        auteurNom: "Dupont",
        auteurPrenom: "Marie",
        auteurEmail: "marie@learnhub.fr",
        auteurRole: "PROFESSEUR",
      },
    ]);
    vi.spyOn(messagingApi, "getInbox").mockResolvedValue([message]);
    vi.spyOn(messagingApi, "getOutbox").mockResolvedValue([]);

    const result = await dashboardApi.admin();

    expect(result).toMatchObject({
      totalUsers: 4,
      activeUsers: 3,
      students: 2,
      professors: 1,
      administrators: 1,
      activeCourses: 9,
      reportsNew: 1,
      reportsInProgress: 0,
      reportsResolved: 1,
      mailboxMessages: 1,
      unreadMessages: 1,
    });
  });
});
