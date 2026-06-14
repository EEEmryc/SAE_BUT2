import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { describe, expect, it, vi } from "vitest";
import { EnrollmentNotifications } from "./EnrollmentNotifications";

vi.mock("../features/courses/hooks/useCourses", () => ({
  usePendingEnrollmentRequests: () => ({
    data: [
      {
        id: 12,
        statut: "EN_ATTENTE",
        dateInscription: "2026-06-14T18:00:00",
        coursId: 7,
        coursTitre: "DATA CENTER",
        eleveId: 4,
        eleveNom: "Martin",
        elevePrenom: "Sophie",
        eleveEmail: "sophie@learnhub.fr",
      },
    ],
    isFetching: false,
    isError: false,
    error: null,
    refetch: vi.fn(),
  }),
}));

describe("EnrollmentNotifications", () => {
  it("affiche les demandes en attente du professeur", async () => {
    render(
      <MemoryRouter>
        <EnrollmentNotifications
          enabled
          professorEmail="professeur@learnhub.test"
        />
      </MemoryRouter>,
    );

    await userEvent.click(
      screen.getByRole("button", {
        name: "1 demande(s) d'inscription en attente",
      }),
    );

    expect(screen.getByText("Demandes d'inscription")).toBeVisible();
    expect(screen.getByText("Sophie Martin")).toBeVisible();
    expect(screen.getByText("Demande l'accès à DATA CENTER")).toBeVisible();
  });
});
