package sae.learnhub.learnhub.api.dto.cours;

import jakarta.validation.constraints.NotBlank;

public record CoursRequest(
        @NotBlank String titre,
        @NotBlank String description,
        String statut,
        Boolean visibleCatalogue
) {}