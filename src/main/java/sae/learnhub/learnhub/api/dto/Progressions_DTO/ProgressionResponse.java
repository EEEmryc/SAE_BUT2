package sae.elearning.api.dto;

import java.time.LocalDateTime;

public record ProgressionResponse(
        Long id,
        String statut,
        Integer pourcentage,
        LocalDateTime dateDebut,
        LocalDateTime dateMiseAJour,
        LocalDateTime dateFin,
        Long eleveId,
        String eleveNom,
        String elevePrenom,
        Long coursId,
        String coursTitre,
        Long chapitreId,
        String chapitreTitre,
        Long ressourceId,
        String ressourceNom
) {}