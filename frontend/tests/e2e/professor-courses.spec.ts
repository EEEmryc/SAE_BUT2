import { expect, test, type Page } from "@playwright/test";

async function mockProfessorSession(page: Page) {
  await page.route("**/api/auth/login", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        token: "professor-access-token",
        refreshToken: "professor-refresh-token",
      }),
    }),
  );
  await page.route("**/api/auth/me", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        id: 2,
        nom: "Lefebvre",
        prenom: "Thomas",
        email: "thomas@learnhub.fr",
        role: "PROFESSEUR",
        statut: "ACTIF",
      }),
    }),
  );
}

test("permet à un professeur de gérer un cours", async ({ page }, testInfo) => {
  await mockProfessorSession(page);
  const courses = [
    {
      id: 1,
      titre: "Programmation Web",
      description:
        "Apprenez les bases du développement web avec HTML, CSS et JavaScript.",
      dateCreation: "2026-06-10T10:00:00",
      statut: "PUBLISHED",
      visibleCatalogue: true,
      profNom: "Lefebvre",
      profPrenom: "Thomas",
      profEmail: "thomas@learnhub.fr",
    },
  ];
  const chapters = [
    {
      id: 11,
      titre: "Introduction au Web",
      contenu: "Présentation du fonctionnement du Web.",
      ordre: 1,
      dateCreation: "2026-06-10T11:00:00",
      coursId: 1,
      coursTitre: "Programmation Web",
    },
  ];
  const resources = [
    {
      id: 31,
      nom: "Introduction au HTML.pdf",
      url: "https://example.com/introduction-html.pdf",
      type: "PDF",
      telechargeable: true,
      tailleOctets: 1258291,
      dateCreation: "2026-06-10T12:00:00",
      chapitreId: 11,
      chapitreTitre: "Introduction au Web",
    },
  ];
  const enrollments = [
    {
      id: 21,
      statut: "VALIDE",
      dateInscription: "2026-06-11T10:00:00",
      coursId: 1,
      coursTitre: "Programmation Web",
      eleveId: 7,
      eleveNom: "Martin",
      elevePrenom: "Sophie",
      eleveEmail: "sophie@learnhub.fr",
    },
  ];

  await page.route("**/api/cours", async (route) => {
    if (route.request().method() === "POST") {
      const payload = route.request().postDataJSON();
      courses.push({
        id: 2,
        ...payload,
        dateCreation: "2026-06-13T20:00:00",
        profNom: "Lefebvre",
        profPrenom: "Thomas",
        profEmail: "thomas@learnhub.fr",
      });
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify(courses.at(-1)),
      });
      return;
    }
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify(courses),
    });
  });
  await page.route("**/api/cours/1", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify(courses[0]),
    }),
  );
  await page.route("**/api/cours/1/summary", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        students: enrollments.filter((item) => item.statut === "VALIDE").length,
        chapters: chapters.length,
        resources: resources.length,
        averageProgress: 68,
      }),
    }),
  );
  await page.route("**/api/cours/1/ressources", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify(resources),
    }),
  );
  await page.route("**/api/cours/1/chapitres", async (route) => {
    if (route.request().method() === "POST") {
      const payload = route.request().postDataJSON();
      chapters.push({
        id: 12,
        ...payload,
        dateCreation: "2026-06-13T20:10:00",
        coursId: 1,
        coursTitre: "Programmation Web",
      });
    }
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify(
        route.request().method() === "POST" ? chapters.at(-1) : chapters,
      ),
    });
  });
  await page.route(
    "**/api/cours/1/chapitres/11/ressources/upload",
    async (route) => {
      if (route.request().method() === "POST") {
        resources.push({
          id: 32,
          nom: "Support CSS.pdf",
          url: "/api/files/resources/support-css.pdf",
          type: "PDF",
          telechargeable: true,
          tailleOctets: 2048,
          dateCreation: "2026-06-13T20:20:00",
          chapitreId: 11,
          chapitreTitre: "Introduction au Web",
        });
      }
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify(
          route.request().method() === "POST" ? resources.at(-1) : resources,
        ),
      });
    },
  );
  await page.route("**/api/inscriptions/cours/1/etudiants", async (route) => {
    if (route.request().method() === "POST") {
      enrollments.push({
        id: 22,
        statut: "VALIDE",
        dateInscription: "2026-06-13T20:30:00",
        coursId: 1,
        coursTitre: "Programmation Web",
        eleveId: 8,
        eleveNom: "Durand",
        elevePrenom: "Lucas",
        eleveEmail: "lucas@learnhub.fr",
      });
    }
    await route.fulfill({
      status: route.request().method() === "POST" ? 201 : 200,
      contentType: "application/json",
      body: JSON.stringify(
        route.request().method() === "POST" ? enrollments.at(-1) : enrollments,
      ),
    });
  });
  await page.route("**/api/inscriptions/etudiants", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify([
        {
          id: 7,
          nom: "Martin",
          prenom: "Sophie",
          email: "sophie@learnhub.fr",
          role: "ETUDIANT",
          statut: "ACTIF",
        },
        {
          id: 8,
          nom: "Durand",
          prenom: "Lucas",
          email: "lucas@learnhub.fr",
          role: "ETUDIANT",
          statut: "ACTIF",
        },
      ]),
    }),
  );

  await page.goto("/login");
  await page.getByLabel("Email").fill("thomas@learnhub.fr");
  await page.getByRole("textbox", { name: "Mot de passe" }).fill("mot-de-passe");
  await page.getByRole("button", { name: "Se connecter" }).click();

  if (testInfo.project.name === "mobile") {
    await page.getByRole("button", { name: "Ouvrir le menu" }).click();
  }
  await page.getByRole("button", { name: "Cours" }).click();
  await page.getByRole("button", { name: "Mes cours" }).click();
  await expect(page.getByRole("heading", { name: "Mes cours" })).toBeVisible();

  await page.getByRole("button", { name: "Créer un cours" }).click();
  const courseDialog = page.getByRole("dialog", { name: "Créer un cours" });
  await courseDialog.getByLabel("Titre du cours").fill("Architecture logicielle");
  await courseDialog
    .getByLabel("Description")
    .fill("Concevoir une architecture maintenable et testable.");
  await courseDialog.getByRole("button", { name: "Enregistrer" }).click();
  await expect(courseDialog).toBeHidden();
  await expect(page.getByText("Cours créé avec succès")).toBeVisible();
  await expect(page.getByText("Architecture logicielle")).toBeVisible();

  await page.getByRole("button", { name: "Gérer le cours" }).first().click();
  await expect(
    page.getByRole("heading", { name: "Programmation Web" }),
  ).toBeVisible();

  await page
    .getByRole("button", { name: "Ajouter un chapitre" })
    .first()
    .click();
  const chapterDialog = page.getByRole("dialog", {
    name: "Ajouter un chapitre",
  });
  await chapterDialog.getByLabel("Titre").fill("HTML5 - Les bases");
  await chapterDialog
    .getByLabel("Contenu")
    .fill("Structure et balises sémantiques.");
  await chapterDialog.getByRole("button", { name: "Enregistrer" }).click();
  await expect(chapterDialog).toBeHidden();
  await expect(page.getByText("HTML5 - Les bases")).toBeVisible();

  await page.getByRole("button", { name: "Ajouter une ressource" }).click();
  const resourceDialog = page.getByRole("dialog", {
    name: "Ajouter une ressource",
  });
  await resourceDialog.getByLabel("Choisir un fichier").setInputFiles({
    name: "Support CSS.pdf",
    mimeType: "application/pdf",
    buffer: Buffer.from("contenu du support"),
  });
  await resourceDialog.getByRole("button", { name: "Uploader" }).click();
  await expect(resourceDialog).toBeHidden();
  await expect(page.getByText("Support CSS.pdf")).toBeVisible();

  await page
    .getByRole("button", { name: "Gérer les inscriptions" })
    .first()
    .click();
  const enrollmentDialog = page.getByRole("dialog", {
    name: "Inscrire un étudiant",
  });
  await enrollmentDialog.getByLabel("Étudiant").click();
  await page.getByRole("option", { name: /Lucas Durand/ }).click();
  await enrollmentDialog.getByRole("button", { name: "Inscrire" }).click();
  await expect(enrollmentDialog).toBeHidden();
  await expect(page.getByText("Lucas Durand")).toBeVisible();

  await page.screenshot({
    path: `test-results/professor-course-${testInfo.project.name}.png`,
    fullPage: true,
  });
});
