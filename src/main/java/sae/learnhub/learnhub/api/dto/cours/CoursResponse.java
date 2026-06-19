package sae.learnhub.learnhub.api.dto.cours;

import java.time.LocalDateTime;

public record CoursResponse(
        Long id,
        String titre,
        String description,
        LocalDateTime dateCreation,
        String statut,
        boolean visibleCatalogue,
        String fichierPrincipalNom,
        String fichierPrincipalUrl,
        String fichierPrincipalType,
        Long fichierPrincipalTailleOctets,
        String profNom,
        String profPrenom,
        String profEmail
) {}
