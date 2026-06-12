package sae.learnhub.learnhub.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.domain.model.Ressource;
import sae.learnhub.learnhub.domain.repository.IRessourceRepository;
import sae.learnhub.learnhub.infrastructure.persistence.entity.RessourceJpaEntity;
import sae.learnhub.learnhub.infrastructure.persistence.mapper.RessourceMapper;
import sae.learnhub.learnhub.infrastructure.persistence.repository.SpringDataRessourceRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RessourceRepositoryImpl implements IRessourceRepository {

    private final SpringDataRessourceRepository springDataRepository;
    private final RessourceMapper mapper;

    @Override
    public Optional<Ressource> findById(Long id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Ressource save(Ressource ressource) {
        RessourceJpaEntity entityToSave = mapper.toEntity(ressource);
        RessourceJpaEntity savedEntity = springDataRepository.save(entityToSave);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public List<Ressource> findByChapitreIdOrderByNomAsc(Long chapitreId) {
        return springDataRepository.findByChapitreIdOrderByNomAsc(chapitreId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByChapitreId(Long chapitreId) {
        springDataRepository.deleteByChapitreId(chapitreId);
    }

    @Override
    public void deleteAll() {
        springDataRepository.deleteAll();
    }
}