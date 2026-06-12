package sae.learnhub.learnhub.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.infrastructure.persistence.entity.UserJpaEntity;

@Component
public class UserMapper {

    public User toDomain(UserJpaEntity entity) {
        if (entity == null) return null;
        
        User user = new User();
        user.setId(entity.getId());
        user.setNom(entity.getNom());
        user.setPrenom(entity.getPrenom());
        user.setEmail(entity.getEmail());
        user.setPassword(entity.getPassword());
        user.setRole(entity.getRole());
        user.setStatut(entity.getStatut());
        user.setDateCreation(entity.getDateCreation());
        user.setResetToken(entity.getResetToken());
        user.setResetTokenExpiration(entity.getResetTokenExpiration());
        return user;
    }

    public UserJpaEntity toEntity(User domain) {
        if (domain == null) return null;
        
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(domain.getId());
        entity.setNom(domain.getNom());
        entity.setPrenom(domain.getPrenom());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setRole(domain.getRole());
        entity.setStatut(domain.getStatut());
        entity.setDateCreation(domain.getDateCreation());
        entity.setResetToken(domain.getResetToken());
        entity.setResetTokenExpiration(domain.getResetTokenExpiration());
        return entity;
    }
}