package sae.learnhub.learnhub.domain.repository;

import sae.learnhub.learnhub.domain.model.Cours;
import java.util.List;
import java.util.Optional;

public interface CoursRepository {

    Optional<Cours> findById(Long id);

    List<Cours> findAll();

    Cours save(Cours cours);

    void deleteById(Long id);

    void deleteAll();

    List<Cours> findByProfEmail(String email);

    List<Cours> findByProfEmailAndStatut(String email, String statut);

    long countByStatut(String statut);
}
