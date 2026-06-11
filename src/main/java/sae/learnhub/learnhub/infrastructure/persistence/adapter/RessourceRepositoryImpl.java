package sae.elearning.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sae.elearning.domain.model.Ressource;
import sae.elearning.domain.repository.RessourceRepository;
import sae.elearning.infrastructure.persistence.entity.RessourceJpaEntity;
import sae.elearning.infrastructure.persistence.mapper.RessourceMapper;
import sae.elearning.infrastructure.persistence.repository.SpringDataRessourceRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RessourceRepositoryImpl implements RessourceRepository {

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
}