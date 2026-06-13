package sae.learnhub.learnhub.api.dto.Messagerie_DTO;

public record DestinataireResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String role
) {
}
