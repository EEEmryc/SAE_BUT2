package sae.elearning.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sae.elearning.domain.model.Progression;
import sae.elearning.infrastructure.persistence.entity.ProgressionJpaEntity;

@Component
@RequiredArgsConstructor
public class ProgressionMapper {

    private final UserMapper userMapper;
    private final CoursMapper coursMapper;
    private final ChapitreMapper chapitreMapper;
    private final RessourceMapper ressourceMapper;

    public Progression toDomain(ProgressionJpaEntity entity) {
        if (entity == null) return null;

        Progression progression = new Progression();
        progression.setId(entity.getId());
        progression.setStatut(entity.getStatut());
        progression.setPourcentage(entity.getPourcentage());
        progression.setDateDebut(entity.getDateDebut());
        progression.setDateMiseAJour(entity.getDateMiseAJour());
        progression.setDateFin(entity.getDateFin());

        if (entity.getEleve() != null) progression.setEleve(userMapper.toDomain(entity.getEleve()));
        if (entity.getCours() != null) progression.setCours(coursMapper.toDomain(entity.getCours()));
        if (entity.getChapitre() != null) progression.setChapitre(chapitreMapper.toDomain(entity.getChapitre()));
        if (entity.getRessource() != null) progression.setRessource(ressourceMapper.toDomain(entity.getRessource()));

        return progression;
    }

    public ProgressionJpaEntity toEntity(Progression domain) {
        if (domain == null) return null;

        ProgressionJpaEntity entity = new ProgressionJpaEntity();
        entity.setId(domain.getId());
        entity.setStatut(domain.getStatut());
        entity.setPourcentage(domain.getPourcentage());
        entity.setDateDebut(domain.getDateDebut());
        entity.setDateMiseAJour(domain.getDateMiseAJour());
        entity.setDateFin(domain.getDateFin());

        if (domain.getEleve() != null) entity.setEleve(userMapper.toEntity(domain.getEleve()));
        if (domain.getCours() != null) entity.setCours(coursMapper.toEntity(domain.getCours()));
        if (domain.getChapitre() != null) entity.setChapitre(chapitreMapper.toEntity(domain.getChapitre()));
        if (domain.getRessource() != null) entity.setRessource(ressourceMapper.toEntity(domain.getRessource()));

        return entity;
    }
}