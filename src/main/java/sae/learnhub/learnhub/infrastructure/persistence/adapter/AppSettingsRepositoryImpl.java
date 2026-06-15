package sae.learnhub.learnhub.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.domain.model.AppSettings;
import sae.learnhub.learnhub.domain.repository.IAppSettingsRepository;
import sae.learnhub.learnhub.infrastructure.persistence.mapper.AppSettingsMapper;
import sae.learnhub.learnhub.infrastructure.persistence.repository.SpringDataAppSettingsRepository;

@Repository
@RequiredArgsConstructor
public class AppSettingsRepositoryImpl implements IAppSettingsRepository {

    private static final Long SETTINGS_ID = 1L;

    private final SpringDataAppSettingsRepository repository;
    private final AppSettingsMapper mapper;

    @Override
    public AppSettings getSettings() {
        return repository.findById(SETTINGS_ID)
                .map(mapper::toDomain)
                .orElseGet(this::createDefaultSettings);
    }

    @Override
    public AppSettings save(AppSettings settings) {
        settings.setId(SETTINGS_ID);
        return mapper.toDomain(repository.save(mapper.toEntity(settings)));
    }

    private AppSettings createDefaultSettings() {
        AppSettings defaults = AppSettings.defaults();
        defaults.setId(SETTINGS_ID);
        return mapper.toDomain(repository.save(mapper.toEntity(defaults)));
    }
}
