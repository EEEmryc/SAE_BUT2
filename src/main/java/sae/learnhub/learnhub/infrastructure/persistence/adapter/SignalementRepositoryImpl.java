package sae.learnhub.learnhub.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sae.learnhub.learnhub.domain.model.Signalement;
import sae.learnhub.learnhub.domain.repository.ISignalementRepository;
import sae.learnhub.learnhub.infrastructure.persistence.mapper.SignalementMapper;
import sae.learnhub.learnhub.infrastructure.persistence.repository.SpringDataSignalementRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SignalementRepositoryImpl implements ISignalementRepository {

    private final SpringDataSignalementRepository springDataRepository;
    private final SignalementMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<Signalement> findAllOrderByDateEnvoiDesc() {
        return springDataRepository.findAllByOrderByDateEnvoiDesc()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Signalement> findById(Long id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Signalement save(Signalement signalement) {
        return mapper.toDomain(springDataRepository.save(mapper.toEntity(signalement)));
    }

    @Override
    @Transactional
    public void deleteAll() {
        springDataRepository.deleteAllInBatch();
    }
}
