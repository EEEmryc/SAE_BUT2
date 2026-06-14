import { expect, test, type Page } from "@playwright/test";

type Role = "ETUDIANT" | "PROFESSEUR" | "ADMIN";

const inbox = [
  {
    id: 1,
    sujet: "Bienvenue sur LearnHub",
    contenu: "Votre espace est pret.",
    dateEnvoi: "2026-06-14T10:00:00",
    lu: false,
    dateLecture: null,
    expediteurId: 9,
    expediteurNom: "Support",
    expediteurPrenom: "LearnHub",
    expediteurEmail: "support@learnhub.fr",
    destinataireId: 1,
    destinataireNom: "Martin",
    destinatairePrenom: "Sophie",
    destinataireEmail: "sophie@learnhub.fr",
  },
];

async function mockAuthentication(page: Page, role: Role) {
  const profiles = {
    ETUDIANT: {
      id: 1,
      nom: "Martin",
      prenom: "Sophie",
      email: "sophie@learnhub.fr",
      role,
      statut: "ACTIF",
    },
    PROFESSEUR: {
      id: 2,
      nom: "AIT HAMI",
      prenom: "Fadma",
      email: "fadma@learnhub.fr",
      role,
      statut: "ACTIF",
    },
    ADMIN: {
      id: 3,
      nom: "AIT HAMI",
      prenom: "Yacine",
      email: "admin@learnhub.fr",
      role,
      statut: "ACTIF",
    },
  };

  await page.route("**/api/auth/login", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        token: `${role.toLowerCase()}-token`,
        refreshToken: `${role.toLowerCase()}-refresh-token`,
      }),
    }),
  );
  await page.route("**/api/auth/me", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify(profiles[role]),
    }),
  );
  await page.route("**/api/messages/recus", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify(inbox),
    }),
  );
  await page.route("**/api/messages/envoyes", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: "[]",
    }),
  );
  await page.route("**/api/messages/non-lus", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({ nonLus: 1 }),
    }),
  );
}

async function login(page: Page, email: string) {
  await page.goto("/login");
  await page.getByLabel("Email").fill(email);
  await page.getByRole("textbox", { name: "Mot de passe" }).fill("Password123!");
  await page.getByRole("button", { name: "Se connecter" }).click();
  await expect(page).toHaveURL(/\/dashboard$/);
}

test("affiche le tableau de bord etudiant", async ({ page }, testInfo) => {
  await mockAuthentication(page, "ETUDIANT");
  await page.route("**/api/progressions", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify([
        {
          coursId: 1,
          coursTitre: "Data Science",
          profNom: "AIT HAMI",
          profPrenom: "Fadma",
          totalChapitres: 8,
          chapitresTermines: 6,
          totalRessources: 18,
          pourcentageGlobal: 75,
          details: [
            {
              id: 11,
              statut: "TERMINE",
              pourcentage: 100,
              dateDebut: "2026-06-13T08:00:00",
              dateMiseAJour: "2026-06-13T10:00:00",
              dateFin: "2026-06-13T10:00:00",
              chapitreId: 11,
              chapitreTitre: "Nettoyage des donnees",
            },
          ],
        },
        {
          coursId: 2,
          coursTitre: "Bases de donnees SQL",
          profNom: "Dupont",
          profPrenom: "Marie",
          totalChapitres: 6,
          chapitresTermines: 3,
          totalRessources: 12,
          pourcentageGlobal: 50,
          details: [],
        },
      ]),
    }),
  );

  await login(page, "sophie@learnhub.fr");

  await expect(page.getByRole("heading", { name: "Bonjour, Sophie !" })).toBeVisible();
  await expect(page.getByText("Progression par cours")).toBeVisible();
  await expect(page.getByText("Data Science", { exact: true }).first()).toBeVisible();
  await page.screenshot({
    path: `test-results/dashboard-etudiant-${testInfo.project.name}.png`,
    fullPage: true,
  });
});

test("affiche le tableau de bord professeur", async ({ page }, testInfo) => {
  await mockAuthentication(page, "PROFESSEUR");
  await page.route("**/api/cours/*/summary", (route) => {
    const courseId = route.request().url().includes("/2/") ? 2 : 1;
    return route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify(
        courseId === 1
          ? { students: 24, chapters: 6, resources: 15, averageProgress: 68 }
          : { students: 16, chapters: 4, resources: 9, averageProgress: 52 },
      ),
    });
  });
  await page.route("**/api/cours", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify([
        {
          id: 1,
          titre: "Data Science",
          description: "Analyse de donnees",
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
          titre: "Machine Learning",
          description: "Modeles supervises",
          dateCreation: "2026-06-11T10:00:00",
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
      ]),
    }),
  );

  await login(page, "fadma@learnhub.fr");

  await expect(page.getByRole("heading", { name: "Bonjour, Fadma !" })).toBeVisible();
  await expect(page.getByText("Inscriptions par cours")).toBeVisible();
  await expect(page.getByText("40")).toBeVisible();
  await page.screenshot({
    path: `test-results/dashboard-professeur-${testInfo.project.name}.png`,
    fullPage: true,
  });
});

test("affiche le tableau de bord administrateur", async ({ page }, testInfo) => {
  await mockAuthentication(page, "ADMIN");
  await page.route("**/api/admin/stats", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({ totalUsers: 128, activeCourses: 16 }),
    }),
  );
  await page.route("**/api/admin/users", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify([
        {
          id: 1,
          nom: "Martin",
          prenom: "Sophie",
          email: "sophie@learnhub.fr",
          role: "ETUDIANT",
          statut: "ACTIF",
          dateCreation: "2026-06-14T09:00:00",
        },
        {
          id: 2,
          nom: "Dupont",
          prenom: "Marie",
          email: "marie@learnhub.fr",
          role: "PROFESSEUR",
          statut: "ACTIF",
          dateCreation: "2026-06-13T09:00:00",
        },
        {
          id: 3,
          nom: "AIT HAMI",
          prenom: "Yacine",
          email: "admin@learnhub.fr",
          role: "ADMIN",
          statut: "ACTIF",
          dateCreation: "2026-06-12T09:00:00",
        },
      ]),
    }),
  );
  await page.route("**/api/signalements", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify([
        {
          id: 1,
          sujet: "Acces au cours",
          description: "Le cours ne se charge pas.",
          categorie: "ACCES",
          statut: "NOUVEAU",
          dateEnvoi: "2026-06-14T09:30:00",
          pieceJointeNom: null,
          pieceJointeUrl: null,
          auteurId: 1,
          auteurNom: "Martin",
          auteurPrenom: "Sophie",
          auteurEmail: "sophie@learnhub.fr",
          auteurRole: "ETUDIANT",
        },
        {
          id: 2,
          sujet: "Contenu corrige",
          description: "Le probleme a ete traite.",
          categorie: "CONTENU",
          statut: "RESOLU",
          dateEnvoi: "2026-06-13T09:30:00",
          pieceJointeNom: null,
          pieceJointeUrl: null,
          auteurId: 2,
          auteurNom: "Dupont",
          auteurPrenom: "Marie",
          auteurEmail: "marie@learnhub.fr",
          auteurRole: "PROFESSEUR",
        },
      ]),
    }),
  );

  await login(page, "admin@learnhub.fr");

  await expect(page.getByRole("heading", { name: "Bonjour, Yacine !" })).toBeVisible();
  await expect(page.getByText("Répartition des rôles")).toBeVisible();
  await expect(page.getByText("Signalements ouverts")).toBeVisible();
  await page.screenshot({
    path: `test-results/dashboard-admin-${testInfo.project.name}.png`,
    fullPage: true,
  });
});
