package sae.elearning.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sae.elearning.infrastructure.persistence.entity.MessagerieJpaEntity;

import java.util.List;

@Repository
public interface SpringDataMessagerieRepository extends JpaRepository<MessagerieJpaEntity, Long> {

    List<MessagerieJpaEntity> findByDestinataireEmailOrderByDateEnvoiDesc(String email);

    List<MessagerieJpaEntity> findByExpediteurEmailOrderByDateEnvoiDesc(String email);

    long countByDestinataireEmailAndLuFalse(String email);
}