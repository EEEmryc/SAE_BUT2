package sae.elearning.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sae.elearning.infrastructure.persistence.entity.RessourceJpaEntity;
import java.util.List;

@Repository
public interface SpringDataRessourceRepository extends JpaRepository<RessourceJpaEntity, Long> {
    
    List<RessourceJpaEntity> findByChapitreIdOrderByNomAsc(Long chapitreId);
    
    void deleteByChapitreId(Long chapitreId);
}