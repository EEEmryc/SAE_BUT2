package sae.learnhub.learnhub.domain.repository;

import sae.learnhub.learnhub.domain.model.Ressource;

import java.util.List;
import java.util.Optional;

public interface IRessourceRepository {

    List<Ressource> findAll();

    Optional<Ressource> findById(Long id);

    Ressource save(Ressource entity);

    void deleteById(Long id);

    List<Ressource> findByChapitreIdOrderByNomAsc(Long chapitreId);

    void deleteByChapitreId(Long chapitreId);
}
