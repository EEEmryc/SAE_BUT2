package sae.learnhub.learnhub.api.dto.User_DTO;

public record UserResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String role,
        String statut
) {}