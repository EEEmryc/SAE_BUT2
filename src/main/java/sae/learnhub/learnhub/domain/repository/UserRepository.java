package sae.learnhub.learnhub.domain.repository;

import sae.learnhub.learnhub.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);

    List<User> findAll();

    User save(User user);

    void deleteById(Long id);

    void deleteAll();

    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String resetToken);

    List<User> findByRole(String role);

    List<User> findAllStudents();

    long count();
}