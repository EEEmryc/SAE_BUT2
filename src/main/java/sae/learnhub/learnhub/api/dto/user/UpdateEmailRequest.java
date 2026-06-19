package sae.learnhub.learnhub.api.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateEmailRequest(
        @NotBlank(message = "L'email ne peut pas être vide")
        @Email(message = "L'email doit être une adresse valide")
        String newEmail
) {}
