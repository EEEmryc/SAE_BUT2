package sae.elearning.api.dto;

import java.time.LocalDateTime;

public record InscriptionResponse(
        Long id,
        String statut,
        LocalDateTime dateInscription,
        Long coursId,
        String coursTitre,
        Long eleveId,
        String eleveNom,
        String elevePrenom,
        String eleveEmail
) {}