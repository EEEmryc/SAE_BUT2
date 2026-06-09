package sae.elearning.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sae.elearning.infrastructure.persistence.entity.UserJpaEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, Long> {
    
    Optional<UserJpaEntity> findByEmail(String email);
    
    Optional<UserJpaEntity> findByResetToken(String resetToken);
    
    List<UserJpaEntity> findByRole(String role);
    
    @Query("SELECT u FROM UserJpaEntity u WHERE u.role = 'ETUDIANT' OR u.role = 'ROLE_ETUDIANT'")
    List<UserJpaEntity> findAllStudents();
}