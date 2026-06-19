package sae.learnhub.learnhub.api.dto.auth;

public record AuthResponse(
        String token,
        String refreshToken
) {}