package sae.learnhub.learnhub.api.dto.admin;

public record StatsResponse(
        long totalUsers,
        long activeCourses
) {}