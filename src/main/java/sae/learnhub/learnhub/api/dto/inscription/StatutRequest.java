package sae.learnhub.learnhub.api.dto.inscription;

import jakarta.validation.constraints.NotBlank;

public record StatutRequest(
        @NotBlank String statut
) {}