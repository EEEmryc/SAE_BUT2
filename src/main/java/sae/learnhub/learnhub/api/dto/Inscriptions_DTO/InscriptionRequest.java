package sae.learnhub.learnhub.api.dto.Inscriptions_DTO;

import jakarta.validation.constraints.NotNull;

public record InscriptionRequest(
        @NotNull Long eleveId
) {}