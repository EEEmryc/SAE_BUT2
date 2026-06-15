package sae.learnhub.learnhub.api.dto.account;

import jakarta.validation.constraints.NotBlank;

public record AccountRequestStatusRequest(
        @NotBlank(message = "La décision est obligatoire") String statut
) {
}
