package sae.learnhub.learnhub.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import sae.learnhub.learnhub.domain.model.AppSettings;
import sae.learnhub.learnhub.infrastructure.persistence.entity.AppSettingsJpaEntity;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component("appSettingsPersistenceMapper")
public class AppSettingsMapper {

    public AppSettings toDomain(AppSettingsJpaEntity entity) {
        AppSettings settings = new AppSettings();
        settings.setId(entity.getId());
        settings.setRequestableRoles(parseRoles(entity.getRequestableRoles()));
        settings.setInscriptionAutoValidation(entity.isInscriptionAutoValidation());
        return settings;
    }

    public AppSettingsJpaEntity toEntity(AppSettings settings) {
        AppSettingsJpaEntity entity = new AppSettingsJpaEntity();
        entity.setId(settings.getId());
        entity.setRequestableRoles(String.join(",", settings.getRequestableRoles()));
        entity.setInscriptionAutoValidation(settings.isInscriptionAutoValidation());
        return entity;
    }

    private Set<String> parseRoles(String value) {
        if (value == null || value.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .collect(Collectors.toSet());
    }
}
