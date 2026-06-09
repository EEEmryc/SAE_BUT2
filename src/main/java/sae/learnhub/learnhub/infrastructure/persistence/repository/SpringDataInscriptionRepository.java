package sae.elearning.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sae.elearning.infrastructure.persistence.entity.CoursJpaEntity;
import sae.elearning.infrastructure.persistence.entity.InscriptionJpaEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataInscriptionRepository extends JpaRepository<InscriptionJpaEntity, Long> {

    List<InscriptionJpaEntity> findByEleveId(Long eleveId);

    List<InscriptionJpaEntity> findByEleveEmailAndStatut(String email, String statut);

    Optional<InscriptionJpaEntity> findByEleveIdAndCoursId(Long eleveId, Long coursId);

    boolean existsByEleveIdAndCoursId(Long eleveId, Long coursId);

    boolean existsByEleveEmailAndCoursId(String email, Long coursId);

    List<InscriptionJpaEntity> findByCoursId(Long coursId);

    List<InscriptionJpaEntity> findByCoursIdIn(List<Long> coursIds);

    @Query("SELECT i.cours FROM InscriptionJpaEntity i WHERE i.eleve.email = :email")
    List<CoursJpaEntity> findCoursByEleveEmail(@Param("email") String email);

    @Query("SELECT i FROM InscriptionJpaEntity i JOIN FETCH i.cours c JOIN FETCH i.eleve e WHERE c.prof.email = :profEmail ORDER BY c.id, e.nom, e.prenom")
    List<InscriptionJpaEntity> findByCoursProf(@Param("profEmail") String profEmail);
}