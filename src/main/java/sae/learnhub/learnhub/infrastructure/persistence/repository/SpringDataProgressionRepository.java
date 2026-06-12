package sae.learnhub.learnhub.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.infrastructure.persistence.entity.ProgressionJpaEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataProgressionRepository extends JpaRepository<ProgressionJpaEntity, Long> {

    List<ProgressionJpaEntity> findByEleveEmail(String email);

    List<ProgressionJpaEntity> findByEleveEmailAndCoursId(String email, Long coursId);

    Optional<ProgressionJpaEntity> findByEleveEmailAndCoursIdAndChapitreIdAndRessourceId(
            String email, Long coursId, Long chapitreId, Long ressourceId);

    Optional<ProgressionJpaEntity> findByEleveEmailAndChapitreId(String email, Long chapitreId);

    long countByEleveEmailAndCoursIdAndStatut(String email, Long coursId, String statut);
}