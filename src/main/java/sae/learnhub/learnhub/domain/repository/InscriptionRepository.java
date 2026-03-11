package sae.learnhub.learnhub.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.model.Inscription;
import java.util.List;
import java.util.Optional;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
    List<Inscription> findByEleveId(Long eleveId);

    List<Inscription> findByEleveEmailAndStatut(String email, String statut);

    Optional<Inscription> findByEleveIdAndCoursId(Long eleveId, Long coursId);

    boolean existsByEleveIdAndCoursId(Long eleveId, Long coursId);

    boolean existsByEleveEmailAndCoursId(String email, Long coursId);

    List<Inscription> findByCoursId(Long coursId);

    List<Inscription> findByCoursIdIn(List<Long> coursIds);

    @Query("SELECT i.cours FROM Inscription i WHERE i.eleve.email = :email")
    List<Cours> findCoursByEleveEmail(@Param("email") String email);

    @Query("SELECT i FROM Inscription i JOIN FETCH i.cours c JOIN FETCH i.eleve e " +
            "WHERE c.prof.email = :profEmail " +
            "ORDER BY c.id, e.nom, e.prenom")
    List<Inscription> findByCoursProf(@Param("profEmail") String profEmail);
}
