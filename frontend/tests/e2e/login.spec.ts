import { expect, test } from "@playwright/test";

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
