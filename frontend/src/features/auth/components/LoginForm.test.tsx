import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { authApi } from "../api/authApi";
import { LoginForm } from "./LoginForm";

vi.mock("../api/authApi", async () => {
  const actual =
    await vi.importActual<typeof import("../api/authApi")>("../api/authApi");
  return {
    ...actual,
    authApi: {
      ...actual.authApi,
      login: vi.fn(),
      me: vi.fn(),
      forgotPassword: vi.fn(),
    },
  };
});

function renderLoginForm() {
  const queryClient = new QueryClient({
    defaultOptions: {
      mutations: { retry: false },
    },
  });

  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <LoginForm />
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

describe("LoginForm", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("affiche les erreurs lorsque les champs sont vides", async () => {
    const user = userEvent.setup();
    renderLoginForm();

    await user.click(screen.getByRole("button", { name: "Se connecter" }));

    expect(
      await screen.findByText("L’adresse email est obligatoire."),
    ).toBeInTheDocument();
    expect(
      screen.getByText("Le mot de passe est obligatoire."),
    ).toBeInTheDocument();
    expect(authApi.login).not.toHaveBeenCalled();
  });

  it("envoie les identifiants valides à l’API", async () => {
    const user = userEvent.setup();
    vi.mocked(authApi.login).mockResolvedValue({
      token: "access-token",
      refreshToken: "refresh-token",
    });
    vi.mocked(authApi.me).mockResolvedValue({
      id: 1,
      nom: "Martin",
      prenom: "Sophie",
      email: "student@learnhub.fr",
      role: "ETUDIANT",
      statut: "ACTIF",
    });
    renderLoginForm();

    await user.type(
      screen.getByRole("textbox", { name: "Email" }),
      "student@learnhub.fr",
    );
    await user.type(screen.getByLabelText("Mot de passe"), "secret");
    await user.click(screen.getByRole("button", { name: "Se connecter" }));

    expect(authApi.login).toHaveBeenCalledWith({
      email: "student@learnhub.fr",
      password: "secret",
    });
  });
});
