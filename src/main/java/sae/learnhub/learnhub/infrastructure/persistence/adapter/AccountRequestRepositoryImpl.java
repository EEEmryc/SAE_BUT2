package sae.learnhub.learnhub.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.domain.model.AccountRequest;
import sae.learnhub.learnhub.domain.repository.IAccountRequestRepository;
import sae.learnhub.learnhub.infrastructure.persistence.mapper.AccountRequestMapper;
import sae.learnhub.learnhub.infrastructure.persistence.repository.SpringDataAccountRequestRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AccountRequestRepositoryImpl implements IAccountRequestRepository {

    private final SpringDataAccountRequestRepository repository;
    private final AccountRequestMapper mapper;

    @Override
    public AccountRequest save(AccountRequest request) {
        return mapper.toDomain(repository.save(mapper.toEntity(request)));
    }

    @Override
    public Optional<AccountRequest> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<AccountRequest> findAllOrderByDateCreationDesc() {
        return repository.findAllByOrderByDateCreationDesc().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<AccountRequest> findByStatusOrderByDateCreationDesc(String status) {
        return repository.findByStatutOrderByDateCreationDesc(status).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByEmailAndStatus(String email, String status) {
        return repository.existsByEmailIgnoreCaseAndStatut(email, status);
    }

    @Override
    public void deleteAll() {
        repository.deleteAllInBatch();
    }
}
