package sae.learnhub.learnhub.api.mapper;

import org.springframework.stereotype.Component;
import sae.learnhub.learnhub.api.dto.settings.AppSettingsResponse;
import sae.learnhub.learnhub.api.dto.settings.AppSettingsUpdateRequest;
import sae.learnhub.learnhub.application.settings.AppSettingsService;

@Component("appSettingsApiMapper")
public class AppSettingsMapper {

    public AppSettingsResponse toResponse(AppSettingsService.SettingsResult result) {
        return new AppSettingsResponse(result.requestableRoles(), result.inscriptionAutoValidation());
    }

    public AppSettingsService.UpdateCommand toCommand(AppSettingsUpdateRequest request) {
        return new AppSettingsService.UpdateCommand(
                request.requestableRoles(), request.inscriptionAutoValidation());
    }
}
