package sae.learnhub.learnhub.api.dto.Progressions_DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProgressionRequest(
        @NotNull(message = "L'id du cours est obligatoire") 
        Long coursId,
        Long chapitreId,
        Long ressourceId,
        @NotBlank
        String statut,
        @NotNull 
        Integer pourcentage
) {}