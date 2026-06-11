package sae.learnhub.learnhub.domain.repository;

import sae.learnhub.learnhub.domain.model.Messagerie;

import java.util.List;
import java.util.Optional;

public interface IMessagerieRepository {

    List<Messagerie> findAll();

    Optional<Messagerie> findById(Long id);

    Messagerie save(Messagerie entity);

    void deleteById(Long id);

    List<Messagerie> findByDestinataireEmailOrderByDateEnvoiDesc(String email);

    List<Messagerie> findByExpediteurEmailOrderByDateEnvoiDesc(String email);

    long countByDestinataireEmailAndLuFalse(String email);
}
