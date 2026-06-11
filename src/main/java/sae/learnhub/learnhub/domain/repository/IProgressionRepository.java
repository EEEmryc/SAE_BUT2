package sae.learnhub.learnhub.domain.repository;

import sae.learnhub.learnhub.domain.model.Progression;

import java.util.List;
import java.util.Optional;

public interface IProgressionRepository {

    List<Progression> findAll();

    Optional<Progression> findById(Long id);

    Progression save(Progression entity);

    void deleteById(Long id);

    List<Progression> findByEleveEmail(String email);

    List<Progression> findByEleveEmailAndCoursId(String email, Long coursId);

    Optional<Progression> findByEleveEmailAndCoursIdAndChapitreIdAndRessourceId(
            String email, Long coursId, Long chapitreId, Long ressourceId);

    Optional<Progression> findByEleveEmailAndChapitreId(String email, Long chapitreId);

    long countByEleveEmailAndCoursIdAndStatut(String email, Long coursId, String statut);
}
