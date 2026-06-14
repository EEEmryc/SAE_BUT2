package sae.learnhub.learnhub.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.infrastructure.persistence.entity.CoursJpaEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataCoursRepository extends JpaRepository<CoursJpaEntity, Long> {

    List<CoursJpaEntity> findByProfEmail(String email);

    List<CoursJpaEntity> findByProfEmailAndStatut(String email, String statut);

    @EntityGraph(attributePaths = "prof")
    List<CoursJpaEntity> findByVisibleCatalogueTrueAndStatutInOrderByDateCreationDesc(
            List<String> statuts);

    @EntityGraph(attributePaths = "prof")
    Optional<CoursJpaEntity> findByFichierPrincipalUrl(String url);

    long countByStatut(String statut);
}
