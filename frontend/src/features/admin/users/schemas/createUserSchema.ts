import { z } from "zod";

export const createUserSchema = z.object({
  nom: z.string().trim().min(1, "Le nom est obligatoire."),
  prenom: z.string().trim().min(1, "Le prénom est obligatoire."),
  email: z
    .string()
    .trim()
    .min(1, "L'adresse email est obligatoire.")
    .email("Saisissez une adresse email valide."),
  password: z
    .string()
    .min(1, "Le mot de passe provisoire est obligatoire.")
    .min(8, "Le mot de passe doit contenir au moins 8 caractères."),
  role: z.enum(["ADMIN", "PROFESSEUR", "ETUDIANT"], {
    error: "Sélectionnez un rôle.",
  }),
  statut: z.enum(["ACTIF", "INACTIF"], {
    error: "Sélectionnez un statut.",
  }),
});

export type CreateUserFormValues = z.infer<typeof createUserSchema>;
