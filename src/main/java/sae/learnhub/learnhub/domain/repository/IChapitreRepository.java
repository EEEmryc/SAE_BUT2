package sae.learnhub.learnhub.domain.repository;

import sae.learnhub.learnhub.domain.model.Chapitre;

import java.util.List;
import java.util.Optional;

public interface IChapitreRepository {

    List<Chapitre> findAll();

    Optional<Chapitre> findById(Long id);

    Chapitre save(Chapitre entity);

    void deleteById(Long id);

    List<Chapitre> findByCoursIdOrderByOrdreAsc(Long coursId);

    void deleteByCoursId(Long coursId);
}
