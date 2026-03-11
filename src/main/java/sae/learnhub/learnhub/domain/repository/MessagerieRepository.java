package sae.learnhub.learnhub.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.domain.model.Messagerie;

import java.util.List;

@Repository
public interface MessagerieRepository extends JpaRepository<Messagerie, Long> {

    // Messages received by a user (inbox)
    List<Messagerie> findByDestinataireEmailOrderByDateEnvoiDesc(String email);

    // Messages sent by a user (outbox)
    List<Messagerie> findByExpediteurEmailOrderByDateEnvoiDesc(String email);

    // Count unread messages for a user
    long countByDestinataireEmailAndLuFalse(String email);
}
