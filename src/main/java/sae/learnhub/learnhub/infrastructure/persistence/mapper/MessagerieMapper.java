package sae.elearning.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sae.elearning.domain.model.Messagerie;
import sae.elearning.infrastructure.persistence.entity.MessagerieJpaEntity;

@Component
@RequiredArgsConstructor
public class MessagerieMapper {

    private final UserMapper userMapper;

    public Messagerie toDomain(MessagerieJpaEntity entity) {
        if (entity == null) return null;

        Messagerie messagerie = new Messagerie();
        messagerie.setId(entity.getId());
        messagerie.setSujet(entity.getSujet());
        messagerie.setContenu(entity.getContenu());
        messagerie.setDateEnvoi(entity.getDateEnvoi());
        messagerie.setLu(entity.getLu());
        messagerie.setDateLecture(entity.getDateLecture());

        if (entity.getExpediteur() != null) messagerie.setExpediteur(userMapper.toDomain(entity.getExpediteur()));
        if (entity.getDestinataire() != null) messagerie.setDestinataire(userMapper.toDomain(entity.getDestinataire()));

        return messagerie;
    }

    public MessagerieJpaEntity toEntity(Messagerie domain) {
        if (domain == null) return null;

        MessagerieJpaEntity entity = new MessagerieJpaEntity();
        entity.setId(domain.getId());
        entity.setSujet(domain.getSujet());
        entity.setContenu(domain.getContenu());
        entity.setDateEnvoi(domain.getDateEnvoi());
        entity.setLu(domain.getLu());
        entity.setDateLecture(domain.getDateLecture());

        if (domain.getExpediteur() != null) entity.setExpediteur(userMapper.toEntity(domain.getExpediteur()));
        if (domain.getDestinataire() != null) entity.setDestinataire(userMapper.toEntity(domain.getDestinataire()));

        return entity;
    }
}