import { z } from "zod";

export const accountRequestSchema = z.object({
  nom: z.string().trim().min(1, "Le nom est obligatoire."),
  prenom: z.string().trim().min(1, "Le prénom est obligatoire."),
  email: z
    .string()
    .trim()
    .min(1, "L'adresse email est obligatoire.")
    .email("Saisissez une adresse email valide."),
  formation: z
    .string()
    .trim()
    .min(1, "Le diplôme ou la formation est obligatoire."),
  requestedRole: z.enum(["ETUDIANT", "PROFESSEUR"], {
    error: "Sélectionnez un type de compte.",
  }),
  commentaire: z
    .string()
    .trim()
    .min(10, "Expliquez votre demande en au moins 10 caractères.")
    .max(1000, "Le commentaire ne doit pas dépasser 1000 caractères."),
});

export type AccountRequestFormValues = z.infer<typeof accountRequestSchema>;
