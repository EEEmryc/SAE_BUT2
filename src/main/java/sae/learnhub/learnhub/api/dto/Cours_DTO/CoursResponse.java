package sae.learnhub.learnhub.api.dto.Cours_DTO;

import java.time.LocalDateTime;

public record CoursResponse(
        Long id,
        String titre,
        String description,
        LocalDateTime dateCreation,
        String statut,
        boolean visibleCatalogue,
        String profNom,
        String profPrenom,
        String profEmail
) {}