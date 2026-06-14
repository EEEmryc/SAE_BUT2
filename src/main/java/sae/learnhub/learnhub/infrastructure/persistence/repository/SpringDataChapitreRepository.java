package sae.learnhub.learnhub.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.infrastructure.persistence.entity.ChapitreJpaEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataChapitreRepository extends JpaRepository<ChapitreJpaEntity, Long> {
    
    List<ChapitreJpaEntity> findByCoursIdOrderByOrdreAsc(Long coursId);

    @EntityGraph(attributePaths = {"cours", "cours.prof"})
    Optional<ChapitreJpaEntity> findByFichierPrincipalUrl(String url);
    
    void deleteByCoursId(Long coursId);
}
