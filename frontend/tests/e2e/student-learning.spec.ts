import { expect, test, type Page } from "@playwright/test";

const catalogue = [
  {
    id: 42,
    titre: "Data Science avec Python",
    description: "Apprenez les fondamentaux de la data science avec Python.",
    statut: "PUBLISHED",
    profNom: "AIT HAMI",
    profPrenom: "Fadma",
    profEmail: "fadma@learnhub.fr",
    nombreChapitres: 1,
    nombreRessources: 1,
    statutInscription: "VALIDE",
  },
];

const course = {
  id: 42,
  titre: "Data Science avec Python",
  description: "Apprenez les fondamentaux de la data science avec Python.",
  dateCreation: "2026-06-01T10:00:00",
  statut: "PUBLISHED",
  visibleCatalogue: true,
  fichierPrincipalNom: null,
  fichierPrincipalUrl: null,
  fichierPrincipalType: null,
  fichierPrincipalTailleOctets: null,
  profNom: "AIT HAMI",
  profPrenom: "Fadma",
  profEmail: "fadma@learnhub.fr",
};

const chapter = {
  id: 101,
  titre: "Introduction a Python",
  contenu: "Variables, types et premiers scripts Python.",
  ordre: 1,
  dateCreation: "2026-06-02T10:00:00",
  fichierPrincipalNom: null,
  fichierPrincipalUrl: null,
  fichierPrincipalType: null,
  fichierPrincipalTailleOctets: null,
  coursId: 42,
  coursTitre: course.titre,
};

const resource = {
  id: 201,
  nom: "support-python.pdf",
  url: "/api/ressources/201/fichier",
  type: "application/pdf",
  telechargeable: true,
  tailleOctets: 1024,
  dateCreation: "2026-06-03T10:00:00",
  chapitreId: 101,
  chapitreTitre: chapter.titre,
};

function progress(completed: boolean) {
  return {
    coursId: 42,
    coursTitre: course.titre,
    profNom: course.profNom,
    profPrenom: course.profPrenom,
    totalChapitres: 1,
    chapitresTermines: completed ? 1 : 0,
    totalRessources: 1,
    pourcentageGlobal: completed ? 100 : 0,
    details: completed
      ? [
          {
            id: 301,
            statut: "TERMINE",
            pourcentage: 100,
            dateDebut: "2026-06-14T10:00:00",
            dateMiseAJour: "2026-06-14T10:05:00",
            dateFin: "2026-06-14T10:05:00",
            chapitreId: 101,
            chapitreTitre: chapter.titre,
          },
        ]
      : [],
  };
}

async function mockStudentLearning(page: Page) {
  let completed = false;

  await page.route("**/api/auth/login", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        token: "student-access-token",
        refreshToken: "student-refresh-token",
      }),
    }),
  );
  await page.route("**/api/auth/me", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        id: 7,
        nom: "Martin",
        prenom: "Sophie",
        email: "sophie@learnhub.fr",
        role: "ETUDIANT",
        statut: "ACTIF",
      }),
    }),
  );
  await page.route("**/api/cours/catalogue", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify(catalogue),
    }),
  );
  await page.route("**/api/cours/42", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify(course),
    }),
  );
  await page.route("**/api/cours/42/chapitres", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify([chapter]),
    }),
  );
  await page.route("**/api/cours/42/ressources", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify([resource]),
    }),
  );
  await page.route("**/api/progressions/cours/42", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify(progress(completed)),
    }),
  );
  await page.route("**/api/progressions/chapitres/101/commencer", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        ...progress(false).details[0],
        id: 301,
        statut: "EN_COURS",
        pourcentage: 0,
        chapitreId: 101,
        chapitreTitre: chapter.titre,
      }),
    }),
  );
  await page.route("**/api/progressions/chapitres/101/terminer", (route) => {
    completed = true;
    return route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify(progress(true).details[0]),
    });
  });
  await page.route("**/api/progressions", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify([progress(completed)]),
    }),
  );
}

async function login(page: Page) {
  await page.goto("/login");
  await page.getByLabel("Email").fill("sophie@learnhub.fr");
  await page.getByRole("textbox", { name: "Mot de passe" }).fill("Password123!");
  await page.getByRole("button", { name: "Se connecter" }).click();
  await expect(page).toHaveURL(/\/dashboard$/);
}

async function openSidebarOnMobile(page: Page) {
  if ((page.viewportSize()?.width ?? 1024) < 900) {
    await page.getByRole("button", { name: "Ouvrir le menu" }).click();
  }
}

test("un etudiant consulte son cours et suit sa progression", async ({ page }) => {
  await mockStudentLearning(page);
  await login(page);

  await openSidebarOnMobile(page);
  await page.getByRole("button", { name: "Cours" }).click();
  await page.getByRole("button", { name: "Catalogue" }).click();
  await expect(
    page.getByRole("heading", { name: "Catalogue des cours" }),
  ).toBeVisible();
  await expect(page.getByText(course.titre, { exact: true })).toBeVisible();
  await expect(page.getByText("1 chapitres")).toBeVisible();
  await expect(page.getByText("1 ressources")).toBeVisible();

  await page.getByRole("button", { name: "Consulter le cours" }).click();
  await expect(page).toHaveURL(/\/dashboard\/student\/courses\/42$/);
  await expect(page.getByRole("heading", { name: course.titre })).toBeVisible();
  await expect(
    page.getByRole("button", { name: "Modifier le cours" }),
  ).toHaveCount(0);
  await expect(
    page.getByRole("button", { name: "Ajouter un chapitre" }),
  ).toHaveCount(0);

  await page.getByText(chapter.titre).click();
  await expect(page.getByText(resource.nom)).toBeVisible();
  await page.getByRole("button", { name: "Marquer comme termine" }).click();
  await expect(page.getByText("Chapitre marque comme termine")).toBeVisible();
  await expect(page.getByText("100%")).toBeVisible();

  await openSidebarOnMobile(page);
  await page.getByRole("button", { name: "Progression" }).click();
  await expect(page.getByRole("heading", { name: "Ma progression" })).toBeVisible();
  await expect(page.getByText("1/1")).toBeVisible();
  await expect(page.getByText("100%").first()).toBeVisible();
});
