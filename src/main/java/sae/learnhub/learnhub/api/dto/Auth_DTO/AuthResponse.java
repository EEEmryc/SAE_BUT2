package sae.elearning.api.dto;

public record AuthResponse(
        String token,
        String refreshToken
) {}