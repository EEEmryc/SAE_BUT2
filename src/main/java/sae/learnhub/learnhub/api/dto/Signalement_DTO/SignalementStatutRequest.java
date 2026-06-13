package sae.learnhub.learnhub.api.dto.Signalement_DTO;

import jakarta.validation.constraints.NotBlank;

public record SignalementStatutRequest(
        @NotBlank(message = "Le statut est obligatoire")
        String statut
) {
}
