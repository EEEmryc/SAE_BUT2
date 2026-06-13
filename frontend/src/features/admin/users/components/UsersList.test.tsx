import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render, screen, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import {
  adminUsersApi,
  normalizeUserStatus,
} from "../api/adminUsersApi";
import { UsersList } from "./UsersList";

vi.mock("../api/adminUsersApi", async () => {
  const actual =
    await vi.importActual<typeof import("../api/adminUsersApi")>(
      "../api/adminUsersApi",
    );

  return {
    ...actual,
    adminUsersApi: {
      ...actual.adminUsersApi,
      list: vi.fn(),
    },
  };
});

const users = [
  {
    id: 1,
    nom: "Martin",
    prenom: "Sophie",
    email: "sophie.martin@learnhub.fr",
    role: "PROFESSEUR" as const,
    statut: "ACTIF" as const,
    dateCreation: "2026-06-12T10:30:00",
  },
  {
    id: 2,
    nom: "Durand",
    prenom: "Lucas",
    email: "lucas.durand@learnhub.fr",
    role: "ETUDIANT" as const,
    statut: "INACTIF" as const,
    dateCreation: "2026-06-11T09:15:00",
  },
];

function renderList() {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
    },
  });

  return render(
    <QueryClientProvider client={queryClient}>
      <UsersList onCreateUser={vi.fn()} />
    </QueryClientProvider>,
  );
}

describe("UsersList", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("normalise les anciennes valeurs de statut sans les confondre avec INACTIF", () => {
    expect(normalizeUserStatus("Actif")).toBe("ACTIF");
    expect(normalizeUserStatus(" inactif ")).toBe("INACTIF");
  });

  it("charge et affiche les utilisateurs", async () => {
    vi.mocked(adminUsersApi.list).mockResolvedValue(users);
    renderList();

    expect(
      screen.getByLabelText("Chargement des utilisateurs"),
    ).toBeVisible();

    const table = await screen.findByRole("table", {
      name: "Liste des utilisateurs",
    });
    expect(within(table).getByText("Sophie Martin")).toBeVisible();
    expect(within(table).getByText("lucas.durand@learnhub.fr")).toBeVisible();
    expect(within(table).getByText("Professeur")).toBeVisible();
    expect(within(table).getByText("Inactif")).toBeVisible();
  });

  it("filtre la liste avec la recherche", async () => {
    const user = userEvent.setup();
    vi.mocked(adminUsersApi.list).mockResolvedValue(users);
    renderList();

    const table = await screen.findByRole("table", {
      name: "Liste des utilisateurs",
    });
    await user.type(
      screen.getByRole("textbox", { name: "Rechercher un utilisateur" }),
      "lucas",
    );

    expect(within(table).getByText("Lucas Durand")).toBeVisible();
    expect(within(table).queryByText("Sophie Martin")).not.toBeInTheDocument();
  });

  it("affiche une erreur et permet de relancer le chargement", async () => {
    vi.mocked(adminUsersApi.list).mockRejectedValue(
      new Error("Service indisponible"),
    );
    renderList();

    expect(
      await screen.findByText(/Impossible de charger les utilisateurs/),
    ).toBeVisible();
    expect(
      screen.getByRole("button", { name: "Réessayer" }),
    ).toBeVisible();
  });
});
