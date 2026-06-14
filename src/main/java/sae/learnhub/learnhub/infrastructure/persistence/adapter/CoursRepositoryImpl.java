package sae.learnhub.learnhub.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.repository.ICoursRepository;
import sae.learnhub.learnhub.infrastructure.persistence.entity.CoursJpaEntity;
import sae.learnhub.learnhub.infrastructure.persistence.mapper.CoursMapper;
import sae.learnhub.learnhub.domain.repository.IChapitreRepository;
import sae.learnhub.learnhub.domain.repository.IInscriptionRepository;
import sae.learnhub.learnhub.infrastructure.persistence.repository.SpringDataCoursRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CoursRepositoryImpl implements ICoursRepository {

    private final SpringDataCoursRepository springDataRepository;
    private final IChapitreRepository chapitreRepository;
    private final IInscriptionRepository inscriptionRepository;
    private final CoursMapper mapper;

    @Override
    public Optional<Cours> findById(Long id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Cours> findAll() {
        return springDataRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public Cours save(Cours cours) {
        CoursJpaEntity entityToSave;
        if (cours.getId() == null) {
            entityToSave = mapper.toEntity(cours);
        } else {
            entityToSave = springDataRepository.findById(cours.getId())
                    .orElseGet(() -> mapper.toEntity(cours));
            mapper.updateEntity(cours, entityToSave);
        }

        CoursJpaEntity savedEntity = springDataRepository.save(entityToSave);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        inscriptionRepository.deleteAll();
        chapitreRepository.deleteAll();
        springDataRepository.deleteAllInBatch();
    }

    @Override
    public List<Cours> findByProfEmail(String email) {
        return springDataRepository.findByProfEmail(email)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Cours> findByProfEmailAndStatut(String email, String statut) {
        return springDataRepository.findByProfEmailAndStatut(email, statut)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Cours> findVisibleCatalogueByStatuts(List<String> statuts) {
        return springDataRepository
                .findByVisibleCatalogueTrueAndStatutInOrderByDateCreationDesc(statuts)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long countByStatut(String statut) {
        return springDataRepository.countByStatut(statut);
    }
}
