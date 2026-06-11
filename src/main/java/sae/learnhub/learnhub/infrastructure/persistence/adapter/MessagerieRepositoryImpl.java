package sae.elearning.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sae.elearning.domain.model.Messagerie;
import sae.elearning.domain.repository.MessagerieRepository;
import sae.elearning.infrastructure.persistence.entity.MessagerieJpaEntity;
import sae.elearning.infrastructure.persistence.mapper.MessagerieMapper;
import sae.elearning.infrastructure.persistence.repository.SpringDataMessagerieRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MessagerieRepositoryImpl implements MessagerieRepository {

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
}