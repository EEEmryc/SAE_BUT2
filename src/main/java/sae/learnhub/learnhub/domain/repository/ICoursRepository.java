package sae.learnhub.learnhub.domain.repository;

import sae.learnhub.learnhub.domain.model.Cours;

import java.util.List;
import java.util.Optional;

public interface ICoursRepository {

    List<Cours> findAll();

    Optional<Cours> findById(Long id);

    Cours save(Cours entity);

    void deleteById(Long id);

    List<Cours> findByProfEmail(String email);

    List<Cours> findByProfEmailAndStatut(String email, String statut);

    List<Cours> findAllByOrderByDateCreationDesc();
}
