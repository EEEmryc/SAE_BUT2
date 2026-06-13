import { z } from "zod";

export const loginSchema = z.object({
  email: z
    .string()
    .min(1, "L’adresse email est obligatoire.")
    .email("Saisissez une adresse email valide."),
  password: z.string().min(1, "Le mot de passe est obligatoire."),
});

export const forgotPasswordSchema = z.object({
  email: z
    .string()
    .min(1, "L’adresse email est obligatoire.")
    .email("Saisissez une adresse email valide."),
});

export const resetPasswordSchema = z
  .object({
    password: z
      .string()
      .min(1, "Le nouveau mot de passe est obligatoire.")
      .min(8, "Le mot de passe doit contenir au moins 8 caractères."),
    confirmation: z
      .string()
      .min(1, "Confirmez le nouveau mot de passe."),
  })
  .refine((values) => values.password === values.confirmation, {
    message: "Les mots de passe ne correspondent pas.",
    path: ["confirmation"],
  });

export type LoginFormValues = z.infer<typeof loginSchema>;
export type ForgotPasswordFormValues = z.infer<typeof forgotPasswordSchema>;
export type ResetPasswordFormValues = z.infer<typeof resetPasswordSchema>;
