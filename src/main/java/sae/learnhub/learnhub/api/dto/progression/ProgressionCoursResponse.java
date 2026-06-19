package sae.learnhub.learnhub.api.dto.progression;

import java.util.List;

public record ProgressionCoursResponse(
        Long coursId,
        String coursTitre,
        String profNom,
        String profPrenom,
        Integer totalChapitres,
        Integer chapitresTermines,
        Long totalRessources,
        Integer pourcentageGlobal,
        List<ProgressionResponse> details
) {}
