package sae.elearning.api.mapper;

import org.springframework.stereotype.Component;
import sae.elearning.api.dto.MessagerieRequest;
import sae.elearning.api.dto.MessagerieResponse;
import sae.elearning.application.service.MessagerieService;

@Component
public class MessagerieMapper {

    public MessagerieService.MessagerieCommand toCommand(MessagerieRequest request) {
        return new MessagerieService.MessagerieCommand(
                request.emailDestinataire(),
                request.sujet(),
                request.contenu()
        );
    }

    public MessagerieResponse toResponse(MessagerieService.MessagerieResult result) {
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
                result.destinataireEmail()
        );
    }
}