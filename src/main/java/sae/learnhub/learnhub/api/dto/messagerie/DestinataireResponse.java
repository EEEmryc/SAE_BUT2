package sae.learnhub.learnhub.api.dto.messagerie;

public record DestinataireResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String role
) {
}
