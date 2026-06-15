package sae.learnhub.learnhub.domain.repository;

import sae.learnhub.learnhub.domain.model.AppSettings;

public interface IAppSettingsRepository {

    AppSettings getSettings();

    AppSettings save(AppSettings settings);
}
