package sae.elearning.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RessourceRequest(
        @NotBlank String nom,
        @NotBlank String url,
        @NotBlank String type,
        @NotNull Boolean telechargeable
) {}