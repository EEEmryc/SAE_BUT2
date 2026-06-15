package sae.learnhub.learnhub.api.dto.settings;

import java.util.Set;

public record AppSettingsUpdateRequest(
        Set<String> requestableRoles,
        Boolean inscriptionAutoValidation
) {
}
