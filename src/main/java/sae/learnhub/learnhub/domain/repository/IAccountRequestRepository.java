package sae.learnhub.learnhub.domain.repository;

import sae.learnhub.learnhub.domain.model.AccountRequest;

import java.util.List;
import java.util.Optional;

public interface IAccountRequestRepository {

    AccountRequest save(AccountRequest request);

    Optional<AccountRequest> findById(Long id);

    List<AccountRequest> findAllOrderByDateCreationDesc();

    List<AccountRequest> findByStatusOrderByDateCreationDesc(String status);

    boolean existsByEmailAndStatus(String email, String status);

    void deleteAll();
}
