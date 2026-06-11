package sae.elearning.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sae.elearning.domain.model.RefreshToken;
import sae.elearning.domain.repository.RefreshTokenRepository;
import sae.elearning.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import sae.elearning.infrastructure.persistence.mapper.RefreshTokenMapper;
import sae.elearning.infrastructure.persistence.repository.SpringDataRefreshTokenRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final SpringDataRefreshTokenRepository springDataRepository;
    private final RefreshTokenMapper mapper;

    @Override
    public Optional<RefreshToken> findById(Long id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity entityToSave = mapper.toEntity(refreshToken);
        RefreshTokenJpaEntity savedEntity = springDataRepository.save(entityToSave);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        springDataRepository.deleteAll();
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return springDataRepository.findByToken(token).map(mapper::toDomain);
    }

    @Override
    public Optional<RefreshToken> findByEmail(String email) {
        return springDataRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public void deleteByEmail(String email) {
        springDataRepository.deleteByEmail(email);
    }
}