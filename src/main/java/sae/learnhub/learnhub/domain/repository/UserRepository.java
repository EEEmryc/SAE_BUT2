package sae.learnhub.learnhub.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sae.learnhub.learnhub.domain.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
