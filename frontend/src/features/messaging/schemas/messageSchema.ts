import { z } from "zod";

export const messageSchema = z.object({
  emailDestinataire: z
    .string()
    .min(1, "Le destinataire est obligatoire.")
    .email("L'adresse email est invalide."),
  sujet: z
    .string()
    .trim()
    .min(1, "Le sujet est obligatoire.")
    .max(255, "Le sujet ne doit pas dépasser 255 caractères."),
  contenu: z
    .string()
    .trim()
    .min(1, "Le contenu est obligatoire.")
    .max(2000, "Le contenu ne doit pas dépasser 2000 caractères."),
});

export type MessageFormValues = z.infer<typeof messageSchema>;
