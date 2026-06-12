package sae.learnhub.learnhub.api.dto.Progressions_DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProgressionRequest(
                @NotNull Long coursId,
                Long chapitreId,
                Long ressourceId,
                @NotBlank String statut,
                @NotNull Integer pourcentage) {
}