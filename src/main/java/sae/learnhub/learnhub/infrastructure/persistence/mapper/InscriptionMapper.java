package sae.learnhub.learnhub.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sae.learnhub.learnhub.domain.model.Inscription;
import sae.learnhub.learnhub.infrastructure.persistence.entity.InscriptionJpaEntity;

@Component
@RequiredArgsConstructor
public class InscriptionMapper {

    private final UserMapper userMapper;
    private final CoursMapper coursMapper;

    public Inscription toDomain(InscriptionJpaEntity entity) {
        if (entity == null) return null;

        Inscription inscription = new Inscription();
        inscription.setId(entity.getId());
        inscription.setDateInscription(entity.getDateInscription());
        inscription.setStatut(entity.getStatut());

        if (entity.getEleve() != null) {
            inscription.setEleve(userMapper.toDomain(entity.getEleve()));
        }

        if (entity.getCours() != null) {
            inscription.setCours(coursMapper.toDomain(entity.getCours()));
        }

        return inscription;
    }

    public InscriptionJpaEntity toEntity(Inscription domain) {
        if (domain == null) return null;

        InscriptionJpaEntity entity = new InscriptionJpaEntity();
        entity.setId(domain.getId());
        entity.setDateInscription(domain.getDateInscription());
        entity.setStatut(domain.getStatut());

        if (domain.getEleve() != null) {
            entity.setEleve(userMapper.toEntity(domain.getEleve()));
        }

        if (domain.getCours() != null) {
            entity.setCours(coursMapper.toEntity(domain.getCours()));
        }

        return entity;
    }
}