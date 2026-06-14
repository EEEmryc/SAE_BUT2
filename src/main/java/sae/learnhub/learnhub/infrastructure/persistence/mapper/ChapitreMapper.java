package sae.learnhub.learnhub.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sae.learnhub.learnhub.domain.model.Chapitre;
import sae.learnhub.learnhub.infrastructure.persistence.entity.ChapitreJpaEntity;

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
        chapitre.setFichierPrincipalNom(entity.getFichierPrincipalNom());
        chapitre.setFichierPrincipalUrl(entity.getFichierPrincipalUrl());
        chapitre.setFichierPrincipalType(entity.getFichierPrincipalType());
        chapitre.setFichierPrincipalTailleOctets(entity.getFichierPrincipalTailleOctets());

        if (entity.getCours() != null) {
            chapitre.setCours(coursMapper.toDomain(entity.getCours()));
        }

        return chapitre;
    }

    public ChapitreJpaEntity toEntity(Chapitre domain) {
        if (domain == null) return null;

        ChapitreJpaEntity entity = new ChapitreJpaEntity();
        updateEntity(domain, entity);
        return entity;
    }

    public void updateEntity(Chapitre domain, ChapitreJpaEntity entity) {
        if (domain == null || entity == null) return;

        entity.setId(domain.getId());
        entity.setTitre(domain.getTitre());
        entity.setContenu(domain.getContenu());
        entity.setOrdre(domain.getOrdre());
        entity.setDateCreation(domain.getDateCreation());
        entity.setFichierPrincipalNom(domain.getFichierPrincipalNom());
        entity.setFichierPrincipalUrl(domain.getFichierPrincipalUrl());
        entity.setFichierPrincipalType(domain.getFichierPrincipalType());
        entity.setFichierPrincipalTailleOctets(domain.getFichierPrincipalTailleOctets());

        if (domain.getCours() != null) {
            entity.setCours(coursMapper.toEntity(domain.getCours()));
        }
    }
}
