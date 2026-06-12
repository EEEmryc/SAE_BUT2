package sae.learnhub.learnhub.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.infrastructure.persistence.entity.RessourceJpaEntity;
import java.util.List;

@Repository
public interface SpringDataRessourceRepository extends JpaRepository<RessourceJpaEntity, Long> {
    
    List<RessourceJpaEntity> findByChapitreIdOrderByNomAsc(Long chapitreId);
    
    void deleteByChapitreId(Long chapitreId);
}