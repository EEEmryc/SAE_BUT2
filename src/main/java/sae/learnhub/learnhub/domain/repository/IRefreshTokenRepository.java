package sae.learnhub.learnhub.domain.repository;

import sae.learnhub.learnhub.domain.model.RefreshToken;

import java.util.List;
import java.util.Optional;

public interface IRefreshTokenRepository {

    List<RefreshToken> findAll();

    Optional<RefreshToken> findById(Long id);

    RefreshToken save(RefreshToken entity);

    void deleteById(Long id);

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByEmail(String email);

    void deleteByEmail(String email);
}
