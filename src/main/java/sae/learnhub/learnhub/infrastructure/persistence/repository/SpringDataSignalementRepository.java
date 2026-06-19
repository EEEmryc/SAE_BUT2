package sae.learnhub.learnhub.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.infrastructure.persistence.entity.SignalementJpaEntity;

import java.util.List;

@Repository
public interface SpringDataSignalementRepository extends JpaRepository<SignalementJpaEntity, Long> {

    List<SignalementJpaEntity> findAllByOrderByDateEnvoiDesc();

    List<SignalementJpaEntity> findByAuteurIdOrderByDateEnvoiDesc(Long auteurId);

    @Modifying
    @Query("DELETE FROM SignalementJpaEntity s WHERE s.auteur.id = :auteurId")
    void deleteByAuteurId(@Param("auteurId") Long auteurId);
}
