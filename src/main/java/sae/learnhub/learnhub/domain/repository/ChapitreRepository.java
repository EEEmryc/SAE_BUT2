package sae.learnhub.learnhub.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.domain.model.Chapitre;

import java.util.List;

@Repository
public interface ChapitreRepository extends JpaRepository<Chapitre, Long> {
    
    List<Chapitre> findByCoursIdOrderByOrdreAsc(Long coursId);
    
    void deleteByCoursId(Long coursId);
}
