package sae.learnhub.learnhub.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sae.learnhub.learnhub.domain.model.Messagerie;
import sae.learnhub.learnhub.domain.repository.IMessagerieRepository;
import sae.learnhub.learnhub.infrastructure.persistence.entity.MessagerieJpaEntity;
import sae.learnhub.learnhub.infrastructure.persistence.mapper.MessagerieMapper;
import sae.learnhub.learnhub.infrastructure.persistence.repository.SpringDataMessagerieRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MessagerieRepositoryImpl implements IMessagerieRepository {

    private final SpringDataMessagerieRepository springDataRepository;
    private final MessagerieMapper mapper;

    @Override
    public Optional<Messagerie> findById(Long id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Messagerie save(Messagerie messagerie) {
        MessagerieJpaEntity entityToSave = mapper.toEntity(messagerie);
        MessagerieJpaEntity savedEntity = springDataRepository.save(entityToSave);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public List<Messagerie> findByDestinataireEmailOrderByDateEnvoiDesc(String email) {
        return springDataRepository.findByDestinataireEmailOrderByDateEnvoiDesc(email)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Messagerie> findByExpediteurEmailOrderByDateEnvoiDesc(String email) {
        return springDataRepository.findByExpediteurEmailOrderByDateEnvoiDesc(email)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByDestinataireEmailAndLuFalse(String email) {
        return springDataRepository.countByDestinataireEmailAndLuFalse(email);
    }

    @Override
    @Transactional
    public void deleteAll() {
        springDataRepository.deleteAllInBatch();
    }
}