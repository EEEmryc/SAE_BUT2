package sae.learnhub.learnhub.api.dto.Register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String nom,
        @NotBlank String prenom,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String role,
        String statut
) {}