package sae.learnhub.learnhub.api.dto.Progressions_DTO;

import java.util.List;

public record ProgressionCoursResponse(
        Long coursId,
        String coursTitre,
        Integer totalChapitres,
        Integer chapitresTermines,
        Integer pourcentageGlobal,
        List<ProgressionResponse> details
) {}