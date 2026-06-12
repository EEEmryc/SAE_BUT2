package sae.learnhub.learnhub.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sae.learnhub.learnhub.domain.model.RefreshToken;
import sae.learnhub.learnhub.domain.repository.IRefreshTokenRepository;
import sae.learnhub.learnhub.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import sae.learnhub.learnhub.infrastructure.persistence.mapper.RefreshTokenMapper;
import sae.learnhub.learnhub.infrastructure.persistence.repository.SpringDataRefreshTokenRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements IRefreshTokenRepository {

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
    @Transactional
    public void deleteAll() {
        springDataRepository.deleteAllInBatch();
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