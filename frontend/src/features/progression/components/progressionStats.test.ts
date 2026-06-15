import { describe, expect, it } from "vitest";
import type { ProfessorStudentProgress } from "../services/progressionApi";
import { calculateProgressionStats } from "./progressionStatsCalculator";

function progress(
  overrides: Partial<ProfessorStudentProgress>,
): ProfessorStudentProgress {
  return {
    inscriptionId: 1,
    eleveId: 1,
    eleveNom: "Martin",
    elevePrenom: "Kamel",
    eleveEmail: "kamel@example.com",
    coursId: 1,
    coursTitre: "Cours",
    chapitresTermines: 0,
    totalChapitres: 1,
    pourcentage: 0,
    derniereActivite: null,
    ...overrides,
  };
}

describe("calculateProgressionStats", () => {
  it("compte deux parcours terminés pour un même étudiant", () => {
    const stats = calculateProgressionStats([
      progress({ inscriptionId: 1, coursId: 1, chapitresTermines: 1, pourcentage: 100 }),
      progress({ inscriptionId: 2, coursId: 2, chapitresTermines: 1, pourcentage: 100 }),
    ]);

    expect(stats.trackedStudents).toBe(1);
    expect(stats.completedPaths).toBe(2);
  });

  it("compte uniquement les progressions faibles avec du contenu", () => {
    const stats = calculateProgressionStats([
      progress({ inscriptionId: 1, pourcentage: 20 }),
      progress({ inscriptionId: 2, coursId: 2, pourcentage: 55 }),
      progress({ inscriptionId: 3, coursId: 3, totalChapitres: 0, pourcentage: 0 }),
    ]);

    expect(stats.supportPaths).toBe(1);
  });

  it("calcule la moyenne à partir des chapitres", () => {
    const stats = calculateProgressionStats([
      progress({ inscriptionId: 1, chapitresTermines: 1, totalChapitres: 1, pourcentage: 100 }),
      progress({ inscriptionId: 2, coursId: 2, chapitresTermines: 1, totalChapitres: 3, pourcentage: 33 }),
    ]);

    expect(stats.averageProgress).toBe(50);
  });
});
