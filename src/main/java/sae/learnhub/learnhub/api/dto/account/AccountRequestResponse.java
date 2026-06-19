package sae.learnhub.learnhub.api.dto.account;

import java.time.LocalDateTime;

public record AccountRequestResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String formation,
        String requestedRole,
        String commentaire,
        String statut,
        LocalDateTime dateCreation,
        LocalDateTime dateTraitement,
        boolean confirmationEmailSent
) {
}
