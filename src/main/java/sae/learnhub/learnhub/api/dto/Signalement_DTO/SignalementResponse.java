package sae.learnhub.learnhub.api.dto.Signalement_DTO;

import java.time.LocalDateTime;

public record SignalementResponse(
        Long id,
        String sujet,
        String description,
        String categorie,
        String statut,
        LocalDateTime dateEnvoi,
        String pieceJointeNom,
        String pieceJointeUrl,
        Long auteurId,
        String auteurNom,
        String auteurPrenom,
        String auteurEmail,
        String auteurRole
) {
}
