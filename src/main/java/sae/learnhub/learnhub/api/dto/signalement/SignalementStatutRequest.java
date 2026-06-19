package sae.learnhub.learnhub.api.dto.signalement;

import jakarta.validation.constraints.NotBlank;

public record SignalementStatutRequest(
        @NotBlank(message = "Le statut est obligatoire")
        String statut
) {
}
