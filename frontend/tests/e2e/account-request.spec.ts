import { expect, test } from "@playwright/test";

test("un visiteur envoie une demande de création de compte", async ({
  page,
}, testInfo) => {
  await page.route("**/api/account-requests", async (route) => {
    await route.fulfill({
      status: 201,
      contentType: "application/json",
      body: JSON.stringify({
        id: 42,
        nom: "Martin",
        prenom: "Sophie",
        email: "sophie.martin@example.com",
        formation: "BUT Informatique",
        requestedRole: "ETUDIANT",
        commentaire: "Je souhaite accéder aux cours LearnHub.",
        statut: "EN_ATTENTE",
        dateCreation: "2026-06-15T15:00:00",
        dateTraitement: null,
        confirmationEmailSent: true,
      }),
    });
  });

  await page.goto("/login");
  await page.getByRole("link", { name: "Créer un compte" }).click();
  await expect(page).toHaveURL(/\/register$/);
  await expect(
    page.getByRole("heading", { name: "Demande de création de compte" }),
  ).toBeVisible();

  const submit = page.getByRole("button", { name: "Envoyer la demande" });
  await expect(submit).toBeDisabled();

  await page.getByLabel("Nom", { exact: true }).fill("Martin");
  await page.getByLabel("Prénom", { exact: true }).fill("Sophie");
  await page
    .getByLabel("Adresse e-mail", { exact: true })
    .fill("sophie.martin@example.com");
  await page
    .getByLabel("Diplôme / Formation", { exact: true })
    .fill("BUT Informatique");
  await page
    .getByLabel("Commentaire / Motif de la demande")
    .fill("Je souhaite accéder aux cours LearnHub.");

  await expect(submit).toBeEnabled();
  await submit.click();
  await expect(
    page.getByRole("heading", { name: "Demande envoyée avec succès !" }),
  ).toBeVisible();

  await page.screenshot({
    path: `test-results/account-request-${testInfo.project.name}.png`,
    fullPage: true,
  });
});
