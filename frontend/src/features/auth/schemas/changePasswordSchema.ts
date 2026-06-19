import { z } from "zod";

export const changePasswordSchema = z
  .object({
    currentPassword: z
      .string()
      .min(1, "Le mot de passe actuel est obligatoire."),
    newPassword: z
      .string()
      .min(1, "Le nouveau mot de passe est obligatoire.")
      .min(8, "Le mot de passe doit contenir au moins 8 caractères."),
    confirmation: z
      .string()
      .min(1, "Confirmez le nouveau mot de passe."),
  })
  .refine((values) => values.newPassword === values.confirmation, {
    message: "Les mots de passe ne correspondent pas.",
    path: ["confirmation"],
  });

export type ChangePasswordFormValues = z.infer<typeof changePasswordSchema>;