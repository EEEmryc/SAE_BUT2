package sae.learnhub.learnhub.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sae.learnhub.learnhub.domain.model.Chapitre;
import sae.learnhub.learnhub.domain.repository.IChapitreRepository;
import sae.learnhub.learnhub.infrastructure.persistence.entity.ChapitreJpaEntity;
import sae.learnhub.learnhub.infrastructure.persistence.mapper.ChapitreMapper;
import sae.learnhub.learnhub.infrastructure.persistence.repository.SpringDataRessourceRepository;
import sae.learnhub.learnhub.infrastructure.persistence.repository.SpringDataChapitreRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChapitreRepositoryImpl implements IChapitreRepository {

    private final SpringDataChapitreRepository springDataRepository;
    private final SpringDataRessourceRepository ressourceRepository;
    private final ChapitreMapper mapper;

    @Override
    public Optional<Chapitre> findById(Long id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public Chapitre save(Chapitre chapitre) {
        ChapitreJpaEntity entityToSave;
        if (chapitre.getId() == null) {
            entityToSave = mapper.toEntity(chapitre);
        } else {
            entityToSave = springDataRepository.findById(chapitre.getId())
                    .orElseGet(() -> mapper.toEntity(chapitre));
            mapper.updateEntity(chapitre, entityToSave);
        }
        ChapitreJpaEntity savedEntity = springDataRepository.save(entityToSave);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public List<Chapitre> findByCoursIdOrderByOrdreAsc(Long coursId) {
        return springDataRepository.findByCoursIdOrderByOrdreAsc(coursId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByCoursId(Long coursId) {
        springDataRepository.deleteByCoursId(coursId);
    }

    @Override
    public void deleteAll() {
        ressourceRepository.deleteAllInBatch();
        springDataRepository.deleteAllInBatch();
    }
}
