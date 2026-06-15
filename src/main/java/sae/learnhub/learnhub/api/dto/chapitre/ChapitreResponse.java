package sae.learnhub.learnhub.api.dto.chapitre;

import java.time.LocalDateTime;

public record ChapitreResponse(
        Long id,
        String titre,
        String contenu,
        Integer ordre,
        LocalDateTime dateCreation,
        String fichierPrincipalNom,
        String fichierPrincipalUrl,
        String fichierPrincipalType,
        Long fichierPrincipalTailleOctets,
        Long coursId,
        String coursTitre
) {}
