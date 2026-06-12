package sae.learnhub.learnhub.domain.repository;

import sae.learnhub.learnhub.domain.model.RefreshToken;
import java.util.Optional;

public interface IRefreshTokenRepository {

    Optional<RefreshToken> findById(Long id);

    RefreshToken save(RefreshToken refreshToken);

    void deleteById(Long id);

    void deleteAll();

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByEmail(String email);

    void deleteByEmail(String email);
}