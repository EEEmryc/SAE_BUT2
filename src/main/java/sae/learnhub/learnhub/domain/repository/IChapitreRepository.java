package sae.learnhub.learnhub.domain.repository;

import sae.learnhub.learnhub.domain.model.Chapitre;
import java.util.List;
import java.util.Optional;

public interface IChapitreRepository {

    Optional<Chapitre> findById(Long id);

    Chapitre save(Chapitre chapitre);

    void deleteById(Long id);

    void deleteAll();

    List<Chapitre> findByCoursIdOrderByOrdreAsc(Long coursId);

    Optional<Chapitre> findByFichierPrincipalUrl(String url);

    void deleteByCoursId(Long coursId);
}
