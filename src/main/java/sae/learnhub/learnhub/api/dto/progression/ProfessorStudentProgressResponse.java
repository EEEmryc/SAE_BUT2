package sae.learnhub.learnhub.api.dto.progression;

import java.time.LocalDateTime;

public record ProfessorStudentProgressResponse(
        Long inscriptionId,
        Long eleveId,
        String eleveNom,
        String elevePrenom,
        String eleveEmail,
        Long coursId,
        String coursTitre,
        Integer chapitresTermines,
        Integer totalChapitres,
        Integer pourcentage,
        LocalDateTime derniereActivite
) {
}
