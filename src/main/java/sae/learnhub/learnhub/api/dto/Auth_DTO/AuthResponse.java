package sae.learnhub.learnhub.api.dto.Auth_DTO;

public record AuthResponse(
        String token,
        String refreshToken
) {}