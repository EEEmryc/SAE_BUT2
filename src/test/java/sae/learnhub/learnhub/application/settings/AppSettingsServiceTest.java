package sae.learnhub.learnhub.application.settings;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.domain.model.AppSettings;
import sae.learnhub.learnhub.domain.repository.IAppSettingsRepository;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppSettingsServiceTest {

    @Mock
    private IAppSettingsRepository settingsRepository;

    @InjectMocks
    private AppSettingsService appSettingsService;

    @Test
    void getSettings_retourneLesValeursActuelles() {
        AppSettings settings = AppSettings.defaults();
        when(settingsRepository.getSettings()).thenReturn(settings);

        AppSettingsService.SettingsResult result = appSettingsService.getSettings();

        assertEquals(Set.of("ETUDIANT", "PROFESSEUR"), result.requestableRoles());
        assertEquals(false, result.inscriptionAutoValidation());
    }

    @Test
    void updateSettings_avecRolesVides_lanceErreurMetier() {
        when(settingsRepository.getSettings()).thenReturn(AppSettings.defaults());

        AppSettingsService.UpdateCommand command = new AppSettingsService.UpdateCommand(Set.of(), null);

        assertThrows(BusinessRuleException.class, () -> appSettingsService.updateSettings(command));
    }

    @Test
    void updateSettings_avecRoleInvalide_lanceErreurMetier() {
        when(settingsRepository.getSettings()).thenReturn(AppSettings.defaults());

        AppSettingsService.UpdateCommand command = new AppSettingsService.UpdateCommand(Set.of("ADMIN"), null);

        assertThrows(BusinessRuleException.class, () -> appSettingsService.updateSettings(command));
    }

    @Test
    void updateSettings_avecValeursValides_metAJourEtSauvegarde() {
        when(settingsRepository.getSettings()).thenReturn(AppSettings.defaults());
        when(settingsRepository.save(any(AppSettings.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppSettingsService.UpdateCommand command = new AppSettingsService.UpdateCommand(Set.of("ETUDIANT"), true);

        AppSettingsService.SettingsResult result = appSettingsService.updateSettings(command);

        assertEquals(Set.of("ETUDIANT"), result.requestableRoles());
        assertTrue(result.inscriptionAutoValidation());
    }
}
