package sae.learnhub.learnhub.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.domain.model.Progression;
import sae.learnhub.learnhub.domain.repository.IProgressionRepository;
import sae.learnhub.learnhub.infrastructure.persistence.entity.ProgressionJpaEntity;
import sae.learnhub.learnhub.infrastructure.persistence.mapper.ProgressionMapper;
import sae.learnhub.learnhub.infrastructure.persistence.repository.SpringDataProgressionRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProgressionRepositoryImpl implements IProgressionRepository {

    private final SpringDataProgressionRepository springDataRepository;
    private final ProgressionMapper mapper;

    @Override
    public Optional<Progression> findById(Long id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Progression save(Progression progression) {
        ProgressionJpaEntity entityToSave = mapper.toEntity(progression);
        ProgressionJpaEntity savedEntity = springDataRepository.save(entityToSave);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public List<Progression> findByEleveEmail(String email) {
        return springDataRepository.findByEleveEmail(email)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Progression> findByEleveEmailAndCoursId(String email, Long coursId) {
        return springDataRepository.findByEleveEmailAndCoursId(email, coursId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Progression> findByEleveEmailAndCoursIdAndChapitreIdAndRessourceId(String email, Long coursId, Long chapitreId, Long ressourceId) {
        return springDataRepository.findByEleveEmailAndCoursIdAndChapitreIdAndRessourceId(email, coursId, chapitreId, ressourceId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Progression> findByEleveEmailAndChapitreId(String email, Long chapitreId) {
        return springDataRepository.findByEleveEmailAndChapitreId(email, chapitreId)
                .map(mapper::toDomain);
    }

    @Override
    public long countByEleveEmailAndCoursIdAndStatut(String email, Long coursId, String statut) {
        return springDataRepository.countByEleveEmailAndCoursIdAndStatut(email, coursId, statut);
    }
}