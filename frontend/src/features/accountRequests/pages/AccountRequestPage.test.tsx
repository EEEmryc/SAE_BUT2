import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { AccountRequestPage } from "./AccountRequestPage";

const mutate = vi.fn();

vi.mock("../hooks/useAccountRequests", () => ({
  useSubmitAccountRequest: () => ({
    mutate,
    isPending: false,
    isError: false,
    isSuccess: false,
    error: null,
  }),
}));

describe("AccountRequestPage", () => {
  beforeEach(() => mutate.mockClear());

  it("active l'envoi uniquement lorsque les champs sont valides", async () => {
    const user = userEvent.setup();
    render(
      <MemoryRouter>
        <AccountRequestPage />
      </MemoryRouter>,
    );

    const submit = screen.getByRole("button", { name: "Envoyer la demande" });
    expect(submit).toBeDisabled();

    await user.type(screen.getByLabelText("Nom"), "Martin");
    await user.type(screen.getByLabelText("Prénom"), "Sophie");
    await user.type(screen.getByLabelText("Adresse e-mail"), "sophie@example.com");
    await user.type(screen.getByLabelText("Diplôme / Formation"), "BUT Informatique");
    await user.type(
      screen.getByLabelText("Commentaire / Motif de la demande"),
      "Je souhaite accéder aux cours LearnHub.",
    );

    expect(submit).toBeEnabled();
    await user.click(submit);

    expect(mutate).toHaveBeenCalledWith({
      nom: "Martin",
      prenom: "Sophie",
      email: "sophie@example.com",
      formation: "BUT Informatique",
      requestedRole: "ETUDIANT",
      commentaire: "Je souhaite accéder aux cours LearnHub.",
    });
  });
});
