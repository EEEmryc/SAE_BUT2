package sae.elearning.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sae.elearning.domain.model.User;
import sae.elearning.domain.repository.UserRepository;
import sae.elearning.infrastructure.persistence.entity.UserJpaEntity;
import sae.elearning.infrastructure.persistence.mapper.UserMapper;
import sae.elearning.infrastructure.persistence.repository.SpringDataUserRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final SpringDataUserRepository springDataRepository;
    private final UserMapper mapper;

    @Override
    public Optional<User> findById(Long id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return springDataRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public User save(User user) {
        UserJpaEntity entityToSave = mapper.toEntity(user);
        UserJpaEntity savedEntity = springDataRepository.save(entityToSave);
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
    public Optional<User> findByEmail(String email) {
        return springDataRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByResetToken(String resetToken) {
        return springDataRepository.findByResetToken(resetToken).map(mapper::toDomain);
    }

    @Override
    public List<User> findByRole(String role) {
        return springDataRepository.findByRole(role).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<User> findAllStudents() {
        return springDataRepository.findAllStudents().stream().map(mapper::toDomain).toList();
    }

    @Override
    public long count() {
        return springDataRepository.count();
    }
}