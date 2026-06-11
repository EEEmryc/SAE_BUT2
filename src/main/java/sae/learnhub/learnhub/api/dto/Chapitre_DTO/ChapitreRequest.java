package sae.elearning.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChapitreRequest(
        @NotBlank String titre,
        @NotBlank String contenu,
        @NotNull Integer ordre
) {}