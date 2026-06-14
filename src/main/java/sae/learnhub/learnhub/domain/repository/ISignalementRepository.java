package sae.learnhub.learnhub.domain.repository;

import sae.learnhub.learnhub.domain.model.Signalement;

import java.util.List;
import java.util.Optional;

public interface ISignalementRepository {

    List<Signalement> findAllOrderByDateEnvoiDesc();

    List<Signalement> findByAuteurIdOrderByDateEnvoiDesc(Long auteurId);

    Optional<Signalement> findById(Long id);

    Signalement save(Signalement signalement);

    void deleteAll();
}