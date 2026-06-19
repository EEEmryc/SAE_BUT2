import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { reportsApi, type Report } from "../services/reportsApi";
import { ReportsPage } from "./ReportsPage";

vi.mock("../services/reportsApi", async () => {
  const actual =
    await vi.importActual<typeof import("../services/reportsApi")>(
      "../services/reportsApi",
    );

  return {
    ...actual,
    reportsApi: {
      ...actual.reportsApi,
      list: vi.fn(),
      getById: vi.fn(),
      updateStatus: vi.fn(),
    },
  };
});

const reports: Report[] = [
  {
    id: 12,
    sujet: "Contenu inapproprié dans un cours",
    description: "Le chapitre contient un passage inadapté.",
    categorie: "CONTENU",
    statut: "NOUVEAU",
    dateEnvoi: "2026-06-13T10:30:00",
    pieceJointeNom: "capture.png",
    pieceJointeUrl: "https://files.learnhub.local/capture.png",
    auteurId: 7,
    auteurNom: "Martin",
    auteurPrenom: "Sophie",
    auteurEmail: "sophie@learnhub.fr",
    auteurRole: "ETUDIANT",
  },
  {
    id: 13,
    sujet: "Erreur d'évaluation",
    description: "La note affichée semble incorrecte.",
    categorie: "EVALUATION",
    statut: "EN_COURS",
    dateEnvoi: "2026-06-12T14:20:00",
    pieceJointeNom: null,
    pieceJointeUrl: null,
    auteurId: 8,
    auteurNom: "Dupont",
    auteurPrenom: "Jean",
    auteurEmail: "jean@learnhub.fr",
    auteurRole: "PROFESSEUR",
  },
];

function renderPage() {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });

  return render(
    <QueryClientProvider client={queryClient}>
      <ReportsPage />
    </QueryClientProvider>,
  );
}

describe("ReportsPage", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("affiche les signalements et met à jour leur statut", async () => {
    const user = userEvent.setup();
    vi.mocked(reportsApi.list).mockResolvedValue(reports);
    vi.mocked(reportsApi.getById).mockResolvedValue(reports[0]);
    vi.mocked(reportsApi.updateStatus).mockResolvedValue({
      ...reports[0],
      statut: "RESOLU",
    });

    renderPage();

    expect(
      screen.getByLabelText("Chargement des signalements"),
    ).toBeVisible();
    expect(
      await screen.findByRole("heading", { name: "Signalements" }),
    ).toBeVisible();
    expect(screen.getByTestId("reports-list")).toHaveTextContent(
      "Erreur d'évaluation",
    );
    expect(await screen.findByTestId("report-detail")).toHaveTextContent(
      "Le chapitre contient un passage inadapté.",
    );

    await user.click(
      screen.getByRole("combobox", { name: "Changer le statut" }),
    );
    await user.click(screen.getByRole("option", { name: "Résolu" }));
    await user.click(screen.getByRole("button", { name: "Enregistrer" }));

    expect(reportsApi.updateStatus).toHaveBeenCalledWith(12, "RESOLU");
    expect(
      await screen.findByText("Statut du signalement mis à jour avec succès."),
    ).toBeVisible();
  });

  it("affiche un état vide explicite", async () => {
    vi.mocked(reportsApi.list).mockResolvedValue([]);
    renderPage();

    expect(await screen.findByText("Aucun signalement")).toBeVisible();
    expect(
      screen.getByText("Aucun problème n'a été signalé pour le moment."),
    ).toBeVisible();
  });

  it("affiche une erreur lorsque le chargement échoue", async () => {
    vi.mocked(reportsApi.list).mockRejectedValue(new Error("API indisponible"));
    renderPage();

    expect(
      await screen.findByText(/Impossible de charger les signalements/),
    ).toBeVisible();
  });
});
