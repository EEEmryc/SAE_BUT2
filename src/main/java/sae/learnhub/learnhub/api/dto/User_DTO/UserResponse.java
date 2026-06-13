package sae.learnhub.learnhub.api.dto.User_DTO;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String role,
        String statut,
        LocalDateTime dateCreation
) {}
