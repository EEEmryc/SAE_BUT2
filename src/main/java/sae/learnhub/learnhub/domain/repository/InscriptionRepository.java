package sae.learnhub.learnhub.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.domain.model.Inscription;
import java.util.List;
import java.util.Optional;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
    List<Inscription> findByEleveId(Long eleveId);
    Optional<Inscription> findByEleveIdAndCoursId(Long eleveId, Long coursId);
    boolean existsByEleveIdAndCoursId(Long eleveId, Long coursId);
}