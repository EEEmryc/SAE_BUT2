package sae.learnhub.learnhub.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.infrastructure.persistence.entity.ChapitreJpaEntity;

import java.util.List;

@Repository
public interface SpringDataChapitreRepository extends JpaRepository<ChapitreJpaEntity, Long> {
    
    List<ChapitreJpaEntity> findByCoursIdOrderByOrdreAsc(Long coursId);
    
    void deleteByCoursId(Long coursId);
}