package sae.learnhub.learnhub.domain.repository;

import sae.learnhub.learnhub.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {

    List<User> findAll();

    Optional<User> findById(Long id);

    User save(User entity);

    void deleteById(Long id);

    long count();

    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String resetToken);

    List<User> findByRole(String role);

    List<User> findAllStudents();
}
