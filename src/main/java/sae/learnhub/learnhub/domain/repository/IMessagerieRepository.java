package sae.learnhub.learnhub.domain.repository;

import sae.learnhub.learnhub.domain.model.Messagerie;
import java.util.List;
import java.util.Optional;

public interface MessagerieRepository {

    Optional<Messagerie> findById(Long id);

    Messagerie save(Messagerie messagerie);

    void deleteById(Long id);

    void deleteAll();

    List<Messagerie> findByDestinataireEmailOrderByDateEnvoiDesc(String email);

    List<Messagerie> findByExpediteurEmailOrderByDateEnvoiDesc(String email);

    long countByDestinataireEmailAndLuFalse(String email);
}