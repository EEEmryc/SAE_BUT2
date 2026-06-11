package sae.elearning.api.dto;

import jakarta.validation.constraints.NotNull;

public record InscriptionRequest(
        @NotNull Long eleveId
) {}