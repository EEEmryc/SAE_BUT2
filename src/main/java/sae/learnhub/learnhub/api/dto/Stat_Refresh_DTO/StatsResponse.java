package sae.elearning.api.dto;

public record StatsResponse(
        long totalUsers,
        long activeCourses
) {}