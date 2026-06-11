package sae.learnhub.learnhub.domain.repository;

import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.model.Inscription;

import java.util.List;
import java.util.Optional;

public interface IInscriptionRepository {

    List<Inscription> findAll();

    Optional<Inscription> findById(Long id);

    Inscription save(Inscription entity);

    void deleteById(Long id);

    List<Inscription> findByEleveId(Long eleveId);

    List<Inscription> findByEleveEmailAndStatut(String email, String statut);

    Optional<Inscription> findByEleveIdAndCoursId(Long eleveId, Long coursId);

    boolean existsByEleveIdAndCoursId(Long eleveId, Long coursId);

    boolean existsByEleveEmailAndCoursId(String email, Long coursId);

    List<Inscription> findByCoursId(Long coursId);

    List<Inscription> findByCoursIdIn(List<Long> coursIds);

    List<Cours> findCoursByEleveEmail(String email);

    List<Inscription> findByCoursProf(String profEmail);
}
