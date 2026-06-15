package sae.learnhub.learnhub.application.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.domain.model.AppSettings;
import sae.learnhub.learnhub.domain.model.UserRole;
import sae.learnhub.learnhub.domain.repository.IAppSettingsRepository;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppSettingsService {

    private static final Set<String> ALLOWED_REQUESTABLE_ROLES = Set.of(
            UserRole.ETUDIANT.name(), UserRole.PROFESSEUR.name());

    private final IAppSettingsRepository settingsRepository;

    public record SettingsResult(Set<String> requestableRoles, boolean inscriptionAutoValidation) {
    }

    public record UpdateCommand(Set<String> requestableRoles, Boolean inscriptionAutoValidation) {
    }

    public SettingsResult getSettings() {
        return toResult(settingsRepository.getSettings());
    }

    @Transactional
    public SettingsResult updateSettings(UpdateCommand command) {
        AppSettings settings = settingsRepository.getSettings();

        if (command.requestableRoles() != null) {
            settings.setRequestableRoles(normalizeRequestableRoles(command.requestableRoles()));
        }
        if (command.inscriptionAutoValidation() != null) {
            settings.setInscriptionAutoValidation(command.inscriptionAutoValidation());
        }

        return toResult(settingsRepository.save(settings));
    }

    public Set<String> getRequestableRoles() {
        return settingsRepository.getSettings().getRequestableRoles();
    }

    public boolean isInscriptionAutoValidation() {
        return settingsRepository.getSettings().isInscriptionAutoValidation();
    }

    private Set<String> normalizeRequestableRoles(Set<String> roles) {
        if (roles.isEmpty()) {
            throw new BusinessRuleException(
                    "Au moins un rôle demandable doit être sélectionné");
        }

        Set<String> normalized = roles.stream()
                .map(role -> role == null ? "" : role.trim().toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet());

        if (!ALLOWED_REQUESTABLE_ROLES.containsAll(normalized)) {
            throw new BusinessRuleException(
                    "Rôles invalides. Valeurs acceptées : ETUDIANT, PROFESSEUR");
        }

        return normalized;
    }

    private SettingsResult toResult(AppSettings settings) {
        return new SettingsResult(settings.getRequestableRoles(), settings.isInscriptionAutoValidation());
    }
}
