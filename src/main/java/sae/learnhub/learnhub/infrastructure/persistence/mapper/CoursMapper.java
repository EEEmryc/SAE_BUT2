package sae.learnhub.learnhub.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.infrastructure.persistence.entity.CoursJpaEntity;

@Component
@RequiredArgsConstructor
public class CoursMapper {

    private final UserMapper userMapper;

    public Cours toDomain(CoursJpaEntity entity) {
        if (entity == null) return null;

        Cours cours = new Cours();
        cours.setId(entity.getId());
        cours.setTitre(entity.getTitre());
        cours.setDescription(entity.getDescription());
        cours.setDateCreation(entity.getDateCreation());
        cours.setStatut(entity.getStatut());
        cours.setVisibleCatalogue(entity.isVisibleCatalogue());

        if (entity.getProf() != null) {
            cours.setProf(userMapper.toDomain(entity.getProf()));
        }
        

        return cours;
    }

    public CoursJpaEntity toEntity(Cours domain) {
        if (domain == null) return null;

        CoursJpaEntity entity = new CoursJpaEntity();
        entity.setId(domain.getId());
        entity.setTitre(domain.getTitre());
        entity.setDescription(domain.getDescription());
        entity.setDateCreation(domain.getDateCreation());
        entity.setStatut(domain.getStatut());
        entity.setVisibleCatalogue(domain.isVisibleCatalogue());

        if (domain.getProf() != null) {
            entity.setProf(userMapper.toEntity(domain.getProf()));
        }

        return entity;
    }
}