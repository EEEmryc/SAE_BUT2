package sae.elearning.api.dto;

public record UserResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String role,
        String statut
) {}