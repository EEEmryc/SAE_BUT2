package sae.learnhub.learnhub.api.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AccountRequestCreateRequest(
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 100, message = "Le nom est trop long")
        String nom,
        @NotBlank(message = "Le prénom est obligatoire")
        @Size(max = 100, message = "Le prénom est trop long")
        String prenom,
        @NotBlank(message = "L'adresse email est obligatoire")
        @Email(message = "Le format de l'adresse email est invalide")
        String email,
        @NotBlank(message = "Le diplôme ou la formation est obligatoire")
        @Size(max = 180, message = "La formation est trop longue")
        String formation,
        @NotBlank(message = "Le type de compte est obligatoire")
        String requestedRole,
        @NotBlank(message = "Le commentaire est obligatoire")
        @Size(max = 1000, message = "Le commentaire ne doit pas dépasser 1000 caractères")
        String commentaire
) {
}
