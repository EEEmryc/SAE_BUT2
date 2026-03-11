package sae.learnhub.learnhub.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sae.learnhub.learnhub.domain.model.Message;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByExpediteurIdAndDestinataireIdOrExpediteurIdAndDestinataireIdOrderByDateEnvoiAsc(
        Long expId1, Long destId1, Long expId2, Long destId2
    );
}