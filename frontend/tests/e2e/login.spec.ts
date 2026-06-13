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
