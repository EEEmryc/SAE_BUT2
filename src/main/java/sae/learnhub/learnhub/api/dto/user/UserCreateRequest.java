package sae.learnhub.learnhub.api.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank(message = "Le nom est obligatoire") String nom,
        @NotBlank(message = "Le prénom est obligatoire") String prenom,
        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Le format de l'email est invalide") String email,
        @NotBlank(message = "Le mot de passe provisoire est obligatoire")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères") String password,
        @NotBlank(message = "Le rôle est obligatoire") String role,
        String statut
) {}
