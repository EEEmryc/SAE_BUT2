package sae.learnhub.learnhub.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sae.learnhub.learnhub.domain.model.Cours;
import java.util.List;

public interface CoursRepository extends JpaRepository<Cours, Long> {
 Docker
    List<Cours> findByProfEmail(String email);

    List<Cours> findByProfEmailAndStatut(String email, String statut);
}

    long countByStatut(String statut);
}
main
