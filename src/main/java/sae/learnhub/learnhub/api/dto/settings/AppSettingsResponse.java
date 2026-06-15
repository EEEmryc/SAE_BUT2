package sae.learnhub.learnhub.api.dto.settings;

import java.util.Set;

public record AppSettingsResponse(
        Set<String> requestableRoles,
        boolean inscriptionAutoValidation
) {
}
