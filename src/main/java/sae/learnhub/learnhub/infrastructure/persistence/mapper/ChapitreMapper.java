package sae.elearning.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sae.elearning.domain.model.Chapitre;
import sae.elearning.infrastructure.persistence.entity.ChapitreJpaEntity;

@Component
@RequiredArgsConstructor
public class ChapitreMapper {

    private final CoursMapper coursMapper;

    public Chapitre toDomain(ChapitreJpaEntity entity) {
        if (entity == null) return null;

        Chapitre chapitre = new Chapitre();
        chapitre.setId(entity.getId());
        chapitre.setTitre(entity.getTitre());
        chapitre.setContenu(entity.getContenu());
        chapitre.setOrdre(entity.getOrdre());
        chapitre.setDateCreation(entity.getDateCreation());

        if (entity.getCours() != null) {
            chapitre.setCours(coursMapper.toDomain(entity.getCours()));
        }

        return chapitre;
    }

    public ChapitreJpaEntity toEntity(Chapitre domain) {
        if (domain == null) return null;

        ChapitreJpaEntity entity = new ChapitreJpaEntity();
        entity.setId(domain.getId());
        entity.setTitre(domain.getTitre());
        entity.setContenu(domain.getContenu());
        entity.setOrdre(domain.getOrdre());
        entity.setDateCreation(domain.getDateCreation());

        if (domain.getCours() != null) {
            entity.setCours(coursMapper.toEntity(domain.getCours()));
        }

        return entity;
    }
}