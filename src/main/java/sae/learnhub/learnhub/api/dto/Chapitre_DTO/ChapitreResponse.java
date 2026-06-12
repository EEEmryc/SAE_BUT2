package sae.learnhub.learnhub.api.dto.Chapitre_DTO;

import java.time.LocalDateTime;

public record ChapitreResponse(
        Long id,
        String titre,
        String contenu,
        Integer ordre,
        LocalDateTime dateCreation,
        Long coursId,
        String coursTitre
) {}