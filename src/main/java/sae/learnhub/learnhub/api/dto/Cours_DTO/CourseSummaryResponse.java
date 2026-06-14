package sae.learnhub.learnhub.api.dto.Cours_DTO;

public record CourseSummaryResponse(
        long students,
        long chapters,
        long resources,
        int averageProgress) {
}
