import { z } from "zod";

export const reportCategories = [
  "CONTENU",
  "ACCES",
  "COMPORTEMENT",
  "EVALUATION",
  "TECHNIQUE",
  "COMPTE",
  "MESSAGERIE",
  "AUTRE",
] as const;

export const createReportSchema = z.object({
  sujet: z
    .string()
    .trim()
    .min(1, "Le sujet est obligatoire.")
    .max(150, "Le sujet est trop long."),
  categorie: z.enum(reportCategories, {
    error: "Sélectionnez une catégorie.",
  }),
  description: z
    .string()
    .trim()
    .min(10, "Décrivez le problème (10 caractères minimum)."),
});

export type CreateReportFormValues = z.infer<typeof createReportSchema>;