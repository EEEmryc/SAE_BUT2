package sae.learnhub.learnhub.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sae.learnhub.learnhub.domain.model.Signalement;
import sae.learnhub.learnhub.infrastructure.persistence.entity.SignalementJpaEntity;

@Component
@RequiredArgsConstructor
public class SignalementMapper {

    private final UserMapper userMapper;

    public Signalement toDomain(SignalementJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        Signalement signalement = new Signalement();
        signalement.setId(entity.getId());
        signalement.setSujet(entity.getSujet());
        signalement.setDescription(entity.getDescription());
        signalement.setCategorie(entity.getCategorie());
        signalement.setStatut(entity.getStatut());
        signalement.setDateEnvoi(entity.getDateEnvoi());
        signalement.setPieceJointeNom(entity.getPieceJointeNom());
        signalement.setPieceJointeUrl(entity.getPieceJointeUrl());
        signalement.setAuteur(userMapper.toDomain(entity.getAuteur()));
        return signalement;
    }

    public SignalementJpaEntity toEntity(Signalement domain) {
        if (domain == null) {
            return null;
        }

        SignalementJpaEntity entity = new SignalementJpaEntity();
        entity.setId(domain.getId());
        entity.setSujet(domain.getSujet());
        entity.setDescription(domain.getDescription());
        entity.setCategorie(domain.getCategorie());
        entity.setStatut(domain.getStatut());
        entity.setDateEnvoi(domain.getDateEnvoi());
        entity.setPieceJointeNom(domain.getPieceJointeNom());
        entity.setPieceJointeUrl(domain.getPieceJointeUrl());
        entity.setAuteur(userMapper.toEntity(domain.getAuteur()));
        return entity;
    }
}
