package sae.learnhub.learnhub.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.domain.model.User;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String resetToken);

    List<User> findByRole(String role);

    /**
     * Matches both 'ETUDIANT' and 'ROLE_ETUDIANT' to handle legacy rows
     * where the ROLE_ prefix was accidentally stored.
     */
    @Query("SELECT u FROM User u WHERE u.role = 'ETUDIANT' OR u.role = 'ROLE_ETUDIANT'")
    List<User> findAllStudents();
}
