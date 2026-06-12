package sae.learnhub.learnhub.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sae.learnhub.learnhub.domain.model.Ressource;
import sae.learnhub.learnhub.infrastructure.persistence.entity.RessourceJpaEntity;

@Component
@RequiredArgsConstructor
public class RessourceMapper {

    private final ChapitreMapper chapitreMapper;

    public Ressource toDomain(RessourceJpaEntity entity) {
        if (entity == null) return null;
        
        Ressource ressource = new Ressource();
        ressource.setId(entity.getId());
        ressource.setNom(entity.getNom());
        ressource.setUrl(entity.getUrl());
        ressource.setType(entity.getType());
        ressource.setTelechargeable(entity.getTelechargeable());
        ressource.setDateCreation(entity.getDateCreation());
        
        if (entity.getChapitre() != null) {
            ressource.setChapitre(chapitreMapper.toDomain(entity.getChapitre()));
        }
        
        return ressource;
    }

    public RessourceJpaEntity toEntity(Ressource domain) {
        if (domain == null) return null;
        
        RessourceJpaEntity entity = new RessourceJpaEntity();
        entity.setId(domain.getId());
        entity.setNom(domain.getNom());
        entity.setUrl(domain.getUrl());
        entity.setType(domain.getType());
        entity.setTelechargeable(domain.getTelechargeable());
        entity.setDateCreation(domain.getDateCreation());
        
        if (domain.getChapitre() != null) {
            entity.setChapitre(chapitreMapper.toEntity(domain.getChapitre()));
        }
        
        return entity;
    }
}