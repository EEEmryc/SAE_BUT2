package sae.elearning.api.dto;

import jakarta.validation.constraints.NotBlank;

public record StatutRequest(
        @NotBlank String statut
) {}