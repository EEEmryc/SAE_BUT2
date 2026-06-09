package sae.elearning.api.mapper;

import org.springframework.stereotype.Component;
import sae.elearning.api.dto.InscriptionResponse;
import sae.elearning.api.dto.UserResponse;
import sae.elearning.domain.model.Inscription;
import sae.elearning.domain.model.User;

@Component
public class InscriptionMapper {

    public InscriptionResponse toResponse(Inscription inscription) {
        return new InscriptionResponse(
                inscription.getId(),
                inscription.getStatut(),
                inscription.getDateInscription(),
                inscription.getCours() != null ? inscription.getCours().getId() : null,
                inscription.getCours() != null ? inscription.getCours().getTitre() : null,
                inscription.getEleve() != null ? inscription.getEleve().getId() : null,
                inscription.getEleve() != null ? inscription.getEleve().getNom() : null,
                inscription.getEleve() != null ? inscription.getEleve().getPrenom() : null,
                inscription.getEleve() != null ? inscription.getEleve().getEmail() : null
        );
    }

    public UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getRole(),
                user.getStatut()
        );
    }
}