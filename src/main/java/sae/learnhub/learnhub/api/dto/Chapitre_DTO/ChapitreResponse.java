package sae.elearning.api.dto;

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