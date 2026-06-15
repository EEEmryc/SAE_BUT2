package sae.learnhub.learnhub.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sae.learnhub.learnhub.infrastructure.persistence.entity.AppSettingsJpaEntity;

public interface SpringDataAppSettingsRepository extends JpaRepository<AppSettingsJpaEntity, Long> {
}
