package sae.learnhub.learnhub.api.dto.Messagerie_DTO;

import jakarta.validation.constraints.NotBlank;

public record RepondreRequest(
        @NotBlank String contenu
) {}