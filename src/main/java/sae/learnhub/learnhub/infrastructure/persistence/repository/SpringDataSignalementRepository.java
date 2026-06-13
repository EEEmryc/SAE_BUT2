package sae.learnhub.learnhub.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.infrastructure.persistence.entity.SignalementJpaEntity;

import java.util.List;

@Repository
public interface SpringDataSignalementRepository extends JpaRepository<SignalementJpaEntity, Long> {

    List<SignalementJpaEntity> findAllByOrderByDateEnvoiDesc();
}
