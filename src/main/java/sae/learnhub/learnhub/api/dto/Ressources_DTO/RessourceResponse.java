package sae.elearning.api.dto;

import java.time.LocalDateTime;

public record RessourceResponse(
        Long id,
        String nom,
        String url,
        String type,
        Boolean telechargeable,
        LocalDateTime dateCreation,
        Long chapitreId,
        String chapitreTitre
) {}