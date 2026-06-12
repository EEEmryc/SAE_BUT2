package sae.learnhub.learnhub.api.dto.Stat_Refresh_DTO;

import jakarta.validation.constraints.NotBlank;

public record StatutRequest(
        @NotBlank String statut
) {}