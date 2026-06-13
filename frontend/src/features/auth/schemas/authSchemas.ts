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

export type LoginFormValues = z.infer<typeof loginSchema>;
export type ForgotPasswordFormValues = z.infer<typeof forgotPasswordSchema>;
