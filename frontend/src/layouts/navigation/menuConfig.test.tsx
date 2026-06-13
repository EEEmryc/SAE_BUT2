import { describe, expect, it } from "vitest";
import { getNavigationForRole } from "./menuConfig";

function labelsFor(role: "ETUDIANT" | "PROFESSEUR" | "ADMIN") {
  const items = getNavigationForRole(role);
  return [
    ...items.map((item) => item.label),
    ...items.flatMap((item) =>
      item.children?.map((child) => child.label) ?? [],
    ),
  ];
}

describe("menuConfig", () => {
  it("affiche les fonctionnalités d'apprentissage à l'étudiant", () => {
    const labels = labelsFor("ETUDIANT");

    expect(labels).toContain("Mes cours");
    expect(labels).toContain("Catalogue");
    expect(labels).toContain("Progression");
    expect(labels).not.toContain("Administration");
    expect(labels).not.toContain("Utilisateurs");
  });

  it("affiche les outils de création au professeur", () => {
    const labels = labelsFor("PROFESSEUR");

    expect(labels).toContain("Chapitres");
    expect(labels).toContain("Ressources");
    expect(labels).toContain("Inscriptions");
    expect(labels).not.toContain("Signalements");
  });

  it("réserve les fonctions sensibles à l'administrateur", () => {
    const labels = labelsFor("ADMIN");

    expect(labels).toContain("Administration");
    expect(labels).toContain("Utilisateurs");
    expect(labels).toContain("Signalements");
    expect(labels).not.toContain("Progression");
  });
});
