package sae.learnhub.learnhub.application.port;

public interface TokenProvider {

    String generateToken(String username);

    String generateRefreshToken(String username);

    String extractUsername(String token);

    long getRefreshExpirationTime();
}
