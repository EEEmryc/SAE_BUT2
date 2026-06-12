package sae.learnhub.learnhub.api.mapper;

import sae.learnhub.learnhub.api.dto.Messagerie_DTO.MessagerieRequest;
import sae.learnhub.learnhub.api.dto.Messagerie_DTO.MessagerieResponse;
import sae.learnhub.learnhub.application.Messagerie_Service.MessagerieService;
import sae.learnhub.learnhub.domain.model.Messagerie;

public final class MessagerieMapper {

    private MessagerieMapper() {
    }

    public static MessagerieService.MessagerieCommand toCommand(MessagerieRequest request) {
        return new MessagerieService.MessagerieCommand(
                request.emailDestinataire(),
                request.sujet(),
                request.contenu());
    }

    public static MessagerieResponse toResponse(MessagerieService.MessagerieResult result) {
        return new MessagerieResponse(
                result.id(),
                result.sujet(),
                result.contenu(),
                result.dateEnvoi(),
                result.lu(),
                result.dateLecture(),
                result.expediteurId(),
                result.expediteurNom(),
                result.expediteurPrenom(),
                result.expediteurEmail(),
                result.destinataireId(),
                result.destinataireNom(),
                result.destinatairePrenom(),
                result.destinataireEmail());
    }

    public static MessagerieResponse toResponse(Messagerie m) {
        return new MessagerieResponse(
                m.getId(),
                m.getSujet(),
                m.getContenu(),
                m.getDateEnvoi(),
                m.getLu(),
                m.getDateLecture(),
                m.getExpediteur().getId(),
                m.getExpediteur().getNom(),
                m.getExpediteur().getPrenom(),
                m.getExpediteur().getEmail(),
                m.getDestinataire().getId(),
                m.getDestinataire().getNom(),
                m.getDestinataire().getPrenom(),
                m.getDestinataire().getEmail());
    }
}
