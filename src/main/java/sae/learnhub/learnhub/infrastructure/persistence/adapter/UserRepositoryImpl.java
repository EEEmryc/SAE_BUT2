package sae.learnhub.learnhub.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.IUserRepository;
import sae.learnhub.learnhub.infrastructure.persistence.entity.UserJpaEntity;
import sae.learnhub.learnhub.infrastructure.persistence.mapper.UserMapper;
import sae.learnhub.learnhub.infrastructure.persistence.repository.SpringDataUserRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements IUserRepository {

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