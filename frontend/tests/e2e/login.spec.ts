import { expect, test } from "@playwright/test";

async function mockStudentSession(page: import("@playwright/test").Page) {
  await page.route("**/api/auth/login", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        token: "access-token",
        refreshToken: "refresh-token",
      }),
    });
  });

  await page.route("**/api/auth/me", async (route) => {
    await route.fulfill({
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
    });
  });
}

async function mockAdminSession(page: import("@playwright/test").Page) {
  await page.route("**/api/auth/login", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        token: "admin-access-token",
        refreshToken: "admin-refresh-token",
      }),
    });
  });

  await page.route("**/api/auth/me", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        id: 1,
        nom: "AIT HAMI",
        prenom: "Yacine",
        email: "admin@learnhub.fr",
        role: "ADMIN",
        statut: "ACTIF",
      }),
    });
  });
}

test("affiche et valide la page de connexion", async ({
  page,
}, testInfo) => {
  await page.goto("/login");

  await expect(
    page.getByRole("heading", { name: "Bienvenue !" }),
  ).toBeVisible();
  await expect(page.getByLabel("Email")).toBeVisible();
  await expect(
    page.getByRole("textbox", { name: "Mot de passe" }),
  ).toBeVisible();

  await page.screenshot({
    path: `test-results/login-${testInfo.project.name}.png`,
    fullPage: true,
  });

  await page.getByRole("button", { name: "Se connecter" }).click();

  await expect(
    page.getByText("L’adresse email est obligatoire."),
  ).toBeVisible();
  await expect(
    page.getByText("Le mot de passe est obligatoire."),
  ).toBeVisible();

  await page.screenshot({
    path: `test-results/login-validation-${testInfo.project.name}.png`,
    fullPage: true,
  });
});

test("affiche le layout étudiant et son menu rétractable", async ({
  page,
}, testInfo) => {
  await mockStudentSession(page);
  await page.goto("/login");

  await page.getByLabel("Email").fill("sophie@learnhub.fr");
  await page
    .getByRole("textbox", { name: "Mot de passe" })
    .fill("mot-de-passe");
  await page.getByRole("button", { name: "Se connecter" }).click();

  await expect(page).toHaveURL(/\/dashboard$/);
  await expect(
    page.getByRole("heading", { name: "Bonjour, Sophie !" }),
  ).toBeVisible();

  const mobileMenuButton = page.getByRole("button", {
    name: "Ouvrir le menu",
  });

  if (testInfo.project.name === "mobile") {
    await mobileMenuButton.click();
    await expect(
      page.getByRole("button", { name: "Tableau de bord" }),
    ).toBeVisible();
  } else {
    await expect(
      page.getByRole("button", { name: "Fermer le menu" }),
    ).toBeVisible();
  }

  await page.getByRole("button", { name: "Cours" }).click();
  await expect(
    page.getByRole("button", { name: "Mes cours" }),
  ).toBeVisible();
  await expect(
    page.getByRole("button", { name: "Catalogue" }),
  ).toBeVisible();
  await expect(
    page.getByRole("button", { name: "Administration" }),
  ).toHaveCount(0);
  await page.waitForTimeout(350);

  await page.screenshot({
    path: `test-results/dashboard-menu-${testInfo.project.name}.png`,
    fullPage: true,
  });

  await page.getByRole("button", { name: "Fermer le menu" }).click();
  if (testInfo.project.name === "mobile") {
    await expect(
      page.getByRole("button", { name: "Tableau de bord" }),
    ).toBeHidden();
  } else {
    await expect(
      page.getByRole("button", { name: "Ouvrir le menu" }),
    ).toBeVisible();
  }
  await page.waitForTimeout(350);

  await page.screenshot({
    path: `test-results/dashboard-collapsed-${testInfo.project.name}.png`,
    fullPage: true,
  });
});

test("permet à un administrateur de créer un utilisateur", async ({
  page,
}, testInfo) => {
  await mockAdminSession(page);
  let createdPayload: Record<string, string> | null = null;
  const adminUsers = [
    {
      id: 1,
      nom: "AIT HAMI",
      prenom: "Yacine",
      email: "admin@learnhub.fr",
      role: "ADMIN",
      statut: "ACTIF",
      dateCreation: "2026-06-10T10:30:00",
    },
    {
      id: 7,
      nom: "Martin",
      prenom: "Sophie",
      email: "sophie@learnhub.fr",
      role: "PROFESSEUR",
      statut: "ACTIF",
      dateCreation: "2026-06-11T14:45:00",
    },
  ];

  await page.route("**/api/admin/users", async (route) => {
    if (route.request().method() === "GET") {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify(adminUsers),
      });
      return;
    }

    createdPayload = route.request().postDataJSON();
    const createdUser = {
      id: 42,
      ...createdPayload,
      dateCreation: "2026-06-13T18:30:00",
    };
    adminUsers.unshift(createdUser as (typeof adminUsers)[number]);
    await route.fulfill({
      status: 201,
      contentType: "application/json",
      body: JSON.stringify({
        user: createdUser,
        invitationEmailSent: true,
      }),
    });
  });

  await page.goto("/login");
  await page.getByLabel("Email").fill("admin@learnhub.fr");
  await page
    .getByRole("textbox", { name: "Mot de passe" })
    .fill("mot-de-passe");
  await page.getByRole("button", { name: "Se connecter" }).click();

  if (testInfo.project.name === "mobile") {
    await page.getByRole("button", { name: "Ouvrir le menu" }).click();
  }
  await page.getByRole("button", { name: "Administration" }).click();
  await page.getByRole("button", { name: "Utilisateurs" }).click();

  await expect(
    page.getByRole("heading", { name: "Gestion des utilisateurs" }),
  ).toBeVisible();
  await expect(
    page.getByTestId("users-list"),
  ).toContainText("sophie@learnhub.fr");

  await page.screenshot({
    path: `test-results/admin-users-list-${testInfo.project.name}.png`,
    fullPage: true,
  });

  await page
    .getByRole("tab", { name: "Créer un utilisateur" })
    .click();

  await page
    .getByRole("textbox", { name: "Nom", exact: true })
    .fill("Dupont");
  await page
    .getByRole("textbox", { name: "Prénom", exact: true })
    .fill("Marie");
  await page
    .getByRole("textbox", { name: "Adresse email" })
    .fill("marie.dupont@learnhub.fr");
  await page
    .getByLabel("Mot de passe provisoire")
    .fill("Temporaire123!");
  await page.getByRole("button", { name: "Enregistrer" }).click();

  await expect(
    page.getByText(/Utilisateur créé avec succès/),
  ).toBeVisible();
  expect(createdPayload).toMatchObject({
    nom: "Dupont",
    prenom: "Marie",
    email: "marie.dupont@learnhub.fr",
    password: "Temporaire123!",
    role: "ETUDIANT",
    statut: "ACTIF",
  });

  await page
    .getByRole("tab", { name: "Liste des utilisateurs" })
    .click();
  await expect(
    page.getByTestId("users-list"),
  ).toContainText("marie.dupont@learnhub.fr");

  await page.evaluate(() => window.scrollTo(0, 0));
  await page.screenshot({
    path: `test-results/admin-create-user-${testInfo.project.name}.png`,
    fullPage: true,
  });
});

test("permet de consulter et répondre à un message", async ({
  page,
}, testInfo) => {
  await mockAdminSession(page);
  const receivedMessage = {
    id: 101,
    sujet: "Question sur le cours React avancé",
    contenu:
      "Bonjour, je ne comprends pas la différence entre useMemo et useCallback. Pouvez-vous m'apporter des éclaircissements ?",
    dateEnvoi: "2026-06-13T10:24:00",
    lu: false,
    dateLecture: null,
    expediteurId: 7,
    expediteurNom: "Martin",
    expediteurPrenom: "Sophie",
    expediteurEmail: "sophie@learnhub.fr",
    destinataireId: 1,
    destinataireNom: "AIT HAMI",
    destinatairePrenom: "Yacine",
    destinataireEmail: "admin@learnhub.fr",
  };
  let replyPayload: Record<string, string> | null = null;

  await page.route("**/api/messages/recus", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify([receivedMessage]),
    });
  });
  await page.route("**/api/messages/envoyes", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify([]),
    });
  });
  await page.route("**/api/messages/destinataires", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify([
        {
          id: 7,
          nom: "Martin",
          prenom: "Sophie",
          email: "sophie@learnhub.fr",
          role: "ETUDIANT",
        },
      ]),
    });
  });
  await page.route("**/api/messages/non-lus", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({ nonLus: 1 }),
    });
  });
  await page.route("**/api/messages/101", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        ...receivedMessage,
        lu: true,
        dateLecture: "2026-06-13T10:30:00",
      }),
    });
  });
  await page.route("**/api/messages/101/repondre", async (route) => {
    replyPayload = route.request().postDataJSON();
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        ...receivedMessage,
        id: 102,
        sujet: "Re: Question sur le cours React avancé",
        contenu: replyPayload?.contenu,
        expediteurId: 1,
        expediteurNom: "AIT HAMI",
        expediteurPrenom: "Yacine",
        expediteurEmail: "admin@learnhub.fr",
        destinataireId: 7,
        destinataireNom: "Martin",
        destinatairePrenom: "Sophie",
        destinataireEmail: "sophie@learnhub.fr",
      }),
    });
  });

  await page.goto("/login");
  await page.getByLabel("Email").fill("admin@learnhub.fr");
  await page
    .getByRole("textbox", { name: "Mot de passe" })
    .fill("mot-de-passe");
  await page.getByRole("button", { name: "Se connecter" }).click();

  if (testInfo.project.name === "mobile") {
    await page.getByRole("button", { name: "Ouvrir le menu" }).click();
  }
  await page.getByRole("button", { name: "Messagerie" }).click();

  await expect(
    page.getByRole("heading", { name: "Messagerie" }),
  ).toBeVisible();
  const messageDetail = page.getByTestId("message-detail");
  await expect(
    messageDetail.getByText("Question sur le cours React avancé", {
      exact: true,
    }),
  ).toBeVisible();
  await expect(
    messageDetail.getByText(/useMemo et useCallback/),
  ).toBeVisible();

  await page.getByRole("button", { name: "Répondre" }).click();
  await expect(page.getByText("Répondre au message")).toBeVisible();
  await page
    .getByRole("textbox", { name: "Votre réponse" })
    .fill("Bonjour Sophie, voici une explication détaillée.");
  await page.getByRole("button", { name: "Envoyer" }).click();

  await expect(page.getByText("Message envoyé avec succès.")).toBeVisible();
  expect(replyPayload).toEqual({
    contenu: "Bonjour Sophie, voici une explication détaillée.",
  });

  await page.evaluate(() => window.scrollTo(0, 0));
  await page.screenshot({
    path: `test-results/messaging-${testInfo.project.name}.png`,
    fullPage: true,
  });
});
