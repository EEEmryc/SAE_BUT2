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
        cours.setFichierPrincipalNom(entity.getFichierPrincipalNom());
        cours.setFichierPrincipalUrl(entity.getFichierPrincipalUrl());
        cours.setFichierPrincipalType(entity.getFichierPrincipalType());
        cours.setFichierPrincipalTailleOctets(entity.getFichierPrincipalTailleOctets());

        if (entity.getProf() != null) {
            cours.setProf(userMapper.toDomain(entity.getProf()));
        }
        

        return cours;
    }

    public CoursJpaEntity toEntity(Cours domain) {
        if (domain == null) return null;

        CoursJpaEntity entity = new CoursJpaEntity();
        updateEntity(domain, entity);
        return entity;
    }

    public void updateEntity(Cours domain, CoursJpaEntity entity) {
        if (domain == null || entity == null) return;

        entity.setId(domain.getId());
        entity.setTitre(domain.getTitre());
        entity.setDescription(domain.getDescription());
        entity.setDateCreation(domain.getDateCreation());
        entity.setStatut(domain.getStatut());
        entity.setVisibleCatalogue(domain.isVisibleCatalogue());
        entity.setFichierPrincipalNom(domain.getFichierPrincipalNom());
        entity.setFichierPrincipalUrl(domain.getFichierPrincipalUrl());
        entity.setFichierPrincipalType(domain.getFichierPrincipalType());
        entity.setFichierPrincipalTailleOctets(domain.getFichierPrincipalTailleOctets());

        if (domain.getProf() != null) {
            entity.setProf(userMapper.toEntity(domain.getProf()));
        }
    }
}
