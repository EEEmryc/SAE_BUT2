package sae.learnhub.learnhub.api.dto.inscription;

import jakarta.validation.constraints.NotNull;

public record InscriptionRequest(
        @NotNull Long eleveId
) {}