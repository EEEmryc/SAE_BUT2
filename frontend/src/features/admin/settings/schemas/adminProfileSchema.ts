import { z } from "zod";

export const adminProfileSchema = z.object({
  nom: z.string().trim().min(1, "Le nom est obligatoire."),
  prenom: z.string().trim().min(1, "Le prénom est obligatoire."),
  password: z
    .string()
    .trim()
    .refine(
      (value) => value.length === 0 || value.length >= 8,
      "Le mot de passe doit contenir au moins 8 caractères.",
    ),
});

export type AdminProfileFormValues = z.infer<typeof adminProfileSchema>;
