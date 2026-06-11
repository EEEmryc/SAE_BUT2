package sae.elearning.api.dto;

import jakarta.validation.constraints.NotBlank;

public record RepondreRequest(
        @NotBlank String contenu
) {}