package sae.learnhub.learnhub.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.domain.model.Ressource;
import java.util.List;

@Repository
public interface RessourceRepository extends JpaRepository<Ressource, Long> {
    
    List<Ressource> findByChapitreIdOrderByNomAsc(Long chapitreId);
    
    void deleteByChapitreId(Long chapitreId);
}
