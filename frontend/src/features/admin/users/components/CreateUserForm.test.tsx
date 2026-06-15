import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { adminUsersApi } from "../services/adminUsersApi";
import { CreateUserForm } from "./CreateUserForm";

vi.mock("../services/adminUsersApi", async () => {
  const actual =
    await vi.importActual<typeof import("../services/adminUsersApi")>(
      "../services/adminUsersApi",
    );
  return {
    ...actual,
    adminUsersApi: {
      ...actual.adminUsersApi,
      create: vi.fn(),
    },
  };
});

function renderForm() {
  const queryClient = new QueryClient({
    defaultOptions: {
      mutations: { retry: false },
    },
  });

  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <CreateUserForm />
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

describe("CreateUserForm", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("affiche les erreurs des champs obligatoires", async () => {
    const user = userEvent.setup();
    renderForm();

    await user.click(screen.getByRole("button", { name: "Enregistrer" }));

    expect(await screen.findByText("Le nom est obligatoire.")).toBeVisible();
    expect(screen.getByText("Le prénom est obligatoire.")).toBeVisible();
    expect(
      screen.getByText("L'adresse email est obligatoire."),
    ).toBeVisible();
    expect(
      screen.getByText("Le mot de passe provisoire est obligatoire."),
    ).toBeVisible();
    expect(adminUsersApi.create).not.toHaveBeenCalled();
  });

  it("envoie les valeurs métier attendues à l'API", async () => {
    const user = userEvent.setup();
    vi.mocked(adminUsersApi.create).mockResolvedValue({
      user: {
        id: 42,
        nom: "Dupont",
        prenom: "Marie",
        email: "marie.dupont@learnhub.fr",
        role: "ETUDIANT",
        statut: "ACTIF",
        dateCreation: null,
      },
      invitationEmailSent: true,
    });
    renderForm();

    await user.type(screen.getByRole("textbox", { name: "Nom" }), "Dupont");
    await user.type(
      screen.getByRole("textbox", { name: "Prénom" }),
      "Marie",
    );
    await user.type(
      screen.getByRole("textbox", { name: "Adresse email" }),
      "marie.dupont@learnhub.fr",
    );
    await user.type(
      screen.getByLabelText("Mot de passe provisoire"),
      "Temporaire123!",
    );
    await user.click(screen.getByRole("button", { name: "Enregistrer" }));

    expect(adminUsersApi.create).toHaveBeenCalledWith({
      nom: "Dupont",
      prenom: "Marie",
      email: "marie.dupont@learnhub.fr",
      password: "Temporaire123!",
      role: "ETUDIANT",
      statut: "ACTIF",
    });
    expect(
      await screen.findByText(/Utilisateur créé avec succès/),
    ).toBeVisible();
  });
});
