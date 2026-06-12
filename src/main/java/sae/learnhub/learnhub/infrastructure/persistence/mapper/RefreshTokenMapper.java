package sae.learnhub.learnhub.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import sae.learnhub.learnhub.domain.model.RefreshToken;
import sae.learnhub.learnhub.infrastructure.persistence.entity.RefreshTokenJpaEntity;

@Component
public class RefreshTokenMapper {

    public RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        if (entity == null) return null;

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(entity.getId());
        refreshToken.setToken(entity.getToken());
        refreshToken.setEmail(entity.getEmail());
        refreshToken.setExpiryDate(entity.getExpiryDate());
        refreshToken.setRevoked(entity.isRevoked());

        return refreshToken;
    }

    public RefreshTokenJpaEntity toEntity(RefreshToken domain) {
        if (domain == null) return null;

        RefreshTokenJpaEntity entity = new RefreshTokenJpaEntity();
        entity.setId(domain.getId());
        entity.setToken(domain.getToken());
        entity.setEmail(domain.getEmail());
        entity.setExpiryDate(domain.getExpiryDate());
        entity.setRevoked(domain.isRevoked());

        return entity;
    }
}