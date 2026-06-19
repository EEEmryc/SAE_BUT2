package sae.learnhub.learnhub.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import sae.learnhub.learnhub.domain.model.AccountRequest;
import sae.learnhub.learnhub.infrastructure.persistence.entity.AccountRequestJpaEntity;

@Component("accountRequestPersistenceMapper")
public class AccountRequestMapper {

    public AccountRequest toDomain(AccountRequestJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new AccountRequest(
                entity.getId(),
                entity.getNom(),
                entity.getPrenom(),
                entity.getEmail(),
                entity.getFormation(),
                entity.getRequestedRole(),
                entity.getCommentaire(),
                entity.getStatut(),
                entity.getDateCreation(),
                entity.getDateTraitement());
    }

    public AccountRequestJpaEntity toEntity(AccountRequest domain) {
        AccountRequestJpaEntity entity = new AccountRequestJpaEntity();
        entity.setId(domain.getId());
        entity.setNom(domain.getNom());
        entity.setPrenom(domain.getPrenom());
        entity.setEmail(domain.getEmail());
        entity.setFormation(domain.getFormation());
        entity.setRequestedRole(domain.getRequestedRole());
        entity.setCommentaire(domain.getCommentaire());
        entity.setStatut(domain.getStatut());
        entity.setDateCreation(domain.getDateCreation());
        entity.setDateTraitement(domain.getDateTraitement());
        return entity;
    }
}
