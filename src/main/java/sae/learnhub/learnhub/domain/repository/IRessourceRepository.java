package sae.learnhub.learnhub.domain.repository;

import sae.learnhub.learnhub.domain.model.Ressource;
import java.util.List;
import java.util.Optional;

public interface IRessourceRepository {

    Optional<Ressource> findById(Long id);

    Ressource save(Ressource ressource);

    void deleteById(Long id);

    void deleteAll();

    List<Ressource> findByChapitreIdOrderByNomAsc(Long chapitreId);

    List<Ressource> findByCoursIdOrderByDateCreationDesc(Long coursId);

    Optional<Ressource> findByUrl(String url);

    long countByCoursId(Long coursId);

    void deleteByChapitreId(Long chapitreId);
}
