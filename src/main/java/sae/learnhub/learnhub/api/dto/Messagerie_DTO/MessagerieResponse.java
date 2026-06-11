package sae.elearning.api.dto;

import java.time.LocalDateTime;

public record MessagerieResponse(
        Long id,
        String sujet,
        String contenu,
        LocalDateTime dateEnvoi,
        Boolean lu,
        LocalDateTime dateLecture,
        Long expediteurId,
        String expediteurNom,
        String expediteurPrenom,
        String expediteurEmail,
        Long destinataireId,
        String destinataireNom,
        String destinatairePrenom,
        String destinataireEmail
) {}