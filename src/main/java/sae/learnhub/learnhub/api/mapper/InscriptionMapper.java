package sae.learnhub.learnhub.api.mapper;

import sae.learnhub.learnhub.api.dto.Inscriptions_DTO.InscriptionResponse;
import sae.learnhub.learnhub.api.dto.User_DTO.UserResponse;
import sae.learnhub.learnhub.domain.model.Inscription;
import sae.learnhub.learnhub.domain.model.User;

public final class InscriptionMapper {

    private InscriptionMapper() {
    }

    public static InscriptionResponse toResponse(Inscription inscription) {
        return new InscriptionResponse(
                inscription.getId(),
                inscription.getStatut(),
                inscription.getDateInscription(),
                inscription.getCours() != null ? inscription.getCours().getId() : null,
                inscription.getCours() != null ? inscription.getCours().getTitre() : null,
                inscription.getEleve() != null ? inscription.getEleve().getId() : null,
                inscription.getEleve() != null ? inscription.getEleve().getNom() : null,
                inscription.getEleve() != null ? inscription.getEleve().getPrenom() : null,
                inscription.getEleve() != null ? inscription.getEleve().getEmail() : null);
    }

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getRole(),
                user.getStatut());
    }
}
