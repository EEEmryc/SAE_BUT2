package sae.learnhub.learnhub.api.dto.Ressources_DTO;

import java.time.LocalDateTime;

public record RessourceResponse(
        Long id,
        String nom,
        String url,
        String type,
        Boolean telechargeable,
        Long tailleOctets,
        LocalDateTime dateCreation,
        Long chapitreId,
        String chapitreTitre
) {}
