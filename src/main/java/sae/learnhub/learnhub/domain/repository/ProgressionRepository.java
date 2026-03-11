package sae.learnhub.learnhub.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.domain.model.Progression;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressionRepository extends JpaRepository<Progression, Long> {

    // All progressions for a student
    List<Progression> findByEleveEmail(String email);

    // All progressions for a student in a specific course
    List<Progression> findByEleveEmailAndCoursId(String email, Long coursId);

    // Exact lookup: student + course + chapter (nullable) + resource (nullable)
    Optional<Progression> findByEleveEmailAndCoursIdAndChapitreIdAndRessourceId(
            String email, Long coursId, Long chapitreId, Long ressourceId);
}
