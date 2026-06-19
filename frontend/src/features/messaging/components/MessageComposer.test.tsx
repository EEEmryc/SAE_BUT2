import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { messagingApi, type Message } from "../services/messagingApi";
import { MessageComposer } from "./MessageComposer";

vi.mock("../services/messagingApi", async () => {
  const actual =
    await vi.importActual<typeof import("../services/messagingApi")>(
      "../services/messagingApi",
    );

  return {
    ...actual,
    messagingApi: {
      ...actual.messagingApi,
      getRecipients: vi.fn(),
      send: vi.fn(),
      reply: vi.fn(),
    },
  };
});

const originalMessage: Message = {
  id: 12,
  sujet: "Question sur React",
  contenu: "Pouvez-vous m'aider ?",
  dateEnvoi: "2026-06-13T10:24:00",
  lu: true,
  dateLecture: "2026-06-13T10:30:00",
  expediteurId: 2,
  expediteurNom: "Martin",
  expediteurPrenom: "Sophie",
  expediteurEmail: "sophie@learnhub.fr",
  destinataireId: 1,
  destinataireNom: "Admin",
  destinatairePrenom: "Yacine",
  destinataireEmail: "admin@learnhub.fr",
};

function renderComposer(replyTo: Message | null = null) {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });
  const onSent = vi.fn();
  const onCancelReply = vi.fn();

  render(
    <QueryClientProvider client={queryClient}>
      <MessageComposer
        replyTo={replyTo}
        onSent={onSent}
        onCancelReply={onCancelReply}
      />
    </QueryClientProvider>,
  );

  return { onSent, onCancelReply };
}

describe("MessageComposer", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(messagingApi.getRecipients).mockResolvedValue([
      {
        id: 2,
        nom: "Martin",
        prenom: "Sophie",
        email: "sophie@learnhub.fr",
        role: "ETUDIANT",
      },
    ]);
  });

  it("valide les champs obligatoires", async () => {
    const user = userEvent.setup();
    renderComposer();

    await user.click(screen.getByRole("button", { name: "Envoyer" }));

    expect(
      await screen.findByText("Le destinataire est obligatoire."),
    ).toBeVisible();
    expect(screen.getByText("Le sujet est obligatoire.")).toBeVisible();
    expect(screen.getByText("Le contenu est obligatoire.")).toBeVisible();
    expect(messagingApi.send).not.toHaveBeenCalled();
  });

  it("envoie un nouveau message au destinataire sélectionné", async () => {
    const user = userEvent.setup();
    vi.mocked(messagingApi.send).mockResolvedValue(originalMessage);
    const { onSent } = renderComposer();

    const recipient = await screen.findByRole("combobox", {
      name: "Destinataire",
    });
    await user.click(recipient);
    await user.click(
      await screen.findByRole("option", {
        name: "Sophie Martin · sophie@learnhub.fr",
      }),
    );
    await user.type(screen.getByRole("textbox", { name: "Sujet" }), "Bonjour");
    await user.type(
      screen.getByRole("textbox", { name: "Message" }),
      "Voici un message de test.",
    );
    await user.click(screen.getByRole("button", { name: "Envoyer" }));

    expect(messagingApi.send).toHaveBeenCalledWith({
      emailDestinataire: "sophie@learnhub.fr",
      sujet: "Bonjour",
      contenu: "Voici un message de test.",
    });
    expect(await screen.findByText("Message envoyé avec succès.")).toBeVisible();
    expect(onSent).toHaveBeenCalled();
  });

  it("répond via l'endpoint du message original", async () => {
    const user = userEvent.setup();
    vi.mocked(messagingApi.reply).mockResolvedValue({
      ...originalMessage,
      id: 13,
      sujet: "Re: Question sur React",
      contenu: "Voici ma réponse.",
    });
    renderComposer(originalMessage);

    await user.type(
      screen.getByRole("textbox", { name: "Votre réponse" }),
      "Voici ma réponse.",
    );
    await user.click(screen.getByRole("button", { name: "Envoyer" }));

    expect(messagingApi.reply).toHaveBeenCalledWith(12, "Voici ma réponse.");
  });
});
