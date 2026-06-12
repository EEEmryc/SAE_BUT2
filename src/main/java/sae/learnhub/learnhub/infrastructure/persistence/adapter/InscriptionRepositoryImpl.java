package sae.learnhub.learnhub.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.model.Inscription;
import sae.learnhub.learnhub.domain.repository.IInscriptionRepository;
import sae.learnhub.learnhub.infrastructure.persistence.entity.InscriptionJpaEntity;
import sae.learnhub.learnhub.infrastructure.persistence.mapper.CoursMapper;
import sae.learnhub.learnhub.infrastructure.persistence.mapper.InscriptionMapper;
import sae.learnhub.learnhub.infrastructure.persistence.repository.SpringDataInscriptionRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InscriptionRepositoryImpl implements IInscriptionRepository {

    private final SpringDataInscriptionRepository springDataRepository;
    private final InscriptionMapper mapper;
    private final CoursMapper coursMapper;

    @Override
    public Optional<Inscription> findById(Long id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Inscription save(Inscription inscription) {
        InscriptionJpaEntity entityToSave = mapper.toEntity(inscription);
        InscriptionJpaEntity savedEntity = springDataRepository.save(entityToSave);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        springDataRepository.deleteAll();
    }

    @Override
    public List<Inscription> findByEleveId(Long eleveId) {
        return springDataRepository.findByEleveId(eleveId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Inscription> findByEleveEmailAndStatut(String email, String statut) {
        return springDataRepository.findByEleveEmailAndStatut(email, statut).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Inscription> findByEleveIdAndCoursId(Long eleveId, Long coursId) {
        return springDataRepository.findByEleveIdAndCoursId(eleveId, coursId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEleveIdAndCoursId(Long eleveId, Long coursId) {
        return springDataRepository.existsByEleveIdAndCoursId(eleveId, coursId);
    }

    @Override
    public boolean existsByEleveEmailAndCoursId(String email, Long coursId) {
        return springDataRepository.existsByEleveEmailAndCoursId(email, coursId);
    }

    @Override
    public List<Inscription> findByCoursId(Long coursId) {
        return springDataRepository.findByCoursId(coursId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Inscription> findByCoursIdIn(List<Long> coursIds) {
        return springDataRepository.findByCoursIdIn(coursIds).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Cours> findCoursByEleveEmail(String email) {
        return springDataRepository.findCoursByEleveEmail(email).stream().map(coursMapper::toDomain).toList();
    }

    @Override
    public List<Inscription> findByCoursProf(String profEmail) {
        return springDataRepository.findByCoursProf(profEmail).stream().map(mapper::toDomain).toList();
    }
}