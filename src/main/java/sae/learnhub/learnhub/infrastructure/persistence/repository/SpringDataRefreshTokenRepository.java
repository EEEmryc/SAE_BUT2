package sae.elearning.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sae.elearning.infrastructure.persistence.entity.RefreshTokenJpaEntity;

import java.util.Optional;

@Repository
public interface SpringDataRefreshTokenRepository extends JpaRepository<RefreshTokenJpaEntity, Long> {

    Optional<RefreshTokenJpaEntity> findByToken(String token);
    
    Optional<RefreshTokenJpaEntity> findByEmail(String email);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshTokenJpaEntity r WHERE r.email = :email")
    void deleteByEmail(@Param("email") String email);
}