package sae.elearning.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sae.elearning.domain.model.Chapitre;
import sae.elearning.domain.repository.ChapitreRepository;
import sae.elearning.infrastructure.persistence.entity.ChapitreJpaEntity;
import sae.elearning.infrastructure.persistence.mapper.ChapitreMapper;
import sae.elearning.infrastructure.persistence.repository.SpringDataChapitreRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChapitreRepositoryImpl implements ChapitreRepository {

    private final SpringDataChapitreRepository springDataRepository;
    private final ChapitreMapper mapper;

    @Override
    public Optional<Chapitre> findById(Long id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Chapitre save(Chapitre chapitre) {
        ChapitreJpaEntity entityToSave = mapper.toEntity(chapitre);
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
}