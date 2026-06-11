package sae.elearning.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CoursRequest(
        @NotBlank String titre,
        @NotBlank String description,
        String statut,
        Boolean visibleCatalogue
) {}