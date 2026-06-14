package sae.learnhub.learnhub.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.infrastructure.persistence.entity.RessourceJpaEntity;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataRessourceRepository extends JpaRepository<RessourceJpaEntity, Long> {
    
    List<RessourceJpaEntity> findByChapitreIdOrderByNomAsc(Long chapitreId);

    List<RessourceJpaEntity> findByChapitreCoursIdOrderByDateCreationDesc(Long coursId);

    @EntityGraph(attributePaths = {"chapitre", "chapitre.cours", "chapitre.cours.prof"})
    Optional<RessourceJpaEntity> findByUrl(String url);

    long countByChapitreCoursId(Long coursId);
    
    void deleteByChapitreId(Long chapitreId);
}
