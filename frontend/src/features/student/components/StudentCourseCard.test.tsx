import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import type { CatalogCourse } from "../services/studentLearningApi";
import { StudentCourseCard } from "./StudentCourseCard";

const baseCourse: CatalogCourse = {
  id: 1,
  titre: "Java",
  description: "Programmation orientée objet",
  statut: "PUBLISHED",
  profNom: "Dupont",
  profPrenom: "Marie",
  profEmail: "marie@learnhub.fr",
  nombreChapitres: 4,
  nombreRessources: 8,
  statutInscription: null,
};

describe("StudentCourseCard", () => {
  it("propose une demande pour un cours sans inscription", async () => {
    const onEnroll = vi.fn();
    render(
      <StudentCourseCard
        course={baseCourse}
        index={0}
        enrolling={false}
        onConsult={vi.fn()}
        onEnroll={onEnroll}
      />,
    );

    await userEvent.click(
      screen.getByRole("button", { name: "Demander l'inscription" }),
    );
    expect(onEnroll).toHaveBeenCalledOnce();
  });

  it("bloque une demande déjà refusée", () => {
    render(
      <StudentCourseCard
        course={{ ...baseCourse, statutInscription: "REFUSE" }}
        index={0}
        enrolling={false}
        onConsult={vi.fn()}
        onEnroll={vi.fn()}
      />,
    );

    expect(
      screen.getByRole("button", { name: "Demande refusée" }),
    ).toBeDisabled();
  });

  it("ouvre uniquement un cours avec inscription validée", async () => {
    const onConsult = vi.fn();
    render(
      <StudentCourseCard
        course={{ ...baseCourse, statutInscription: "VALIDE" }}
        index={0}
        enrolling={false}
        onConsult={onConsult}
        onEnroll={vi.fn()}
      />,
    );

    await userEvent.click(
      screen.getByRole("button", { name: "Consulter le cours" }),
    );
    expect(onConsult).toHaveBeenCalledOnce();
  });
});
