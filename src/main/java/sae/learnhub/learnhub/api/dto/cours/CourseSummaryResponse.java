package sae.learnhub.learnhub.api.dto.cours;

public record CourseSummaryResponse(
        long students,
        long chapters,
        long resources,
        int averageProgress) {
}
