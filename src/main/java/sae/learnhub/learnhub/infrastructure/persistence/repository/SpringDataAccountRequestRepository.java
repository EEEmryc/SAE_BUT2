package sae.learnhub.learnhub.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.infrastructure.persistence.entity.AccountRequestJpaEntity;

import java.util.List;

@Repository
public interface SpringDataAccountRequestRepository
        extends JpaRepository<AccountRequestJpaEntity, Long> {

    List<AccountRequestJpaEntity> findAllByOrderByDateCreationDesc();

    List<AccountRequestJpaEntity> findByStatutOrderByDateCreationDesc(String statut);

    boolean existsByEmailIgnoreCaseAndStatut(String email, String statut);
}
