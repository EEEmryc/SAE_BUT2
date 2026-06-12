package sae.learnhub.learnhub.api.dto.Auth_DTO;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank String token,
        @NotBlank String newPassword
) {}