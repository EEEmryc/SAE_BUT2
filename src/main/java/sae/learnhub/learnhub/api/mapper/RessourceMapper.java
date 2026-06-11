package sae.elearning.api.mapper;

import org.springframework.stereotype.Component;
import sae.elearning.api.dto.RessourceRequest;
import sae.elearning.api.dto.RessourceResponse;
import sae.elearning.application.service.RessourceService;

@Component
public class RessourceMapper {

    public RessourceService.RessourceCommand toCommand(RessourceRequest request) {
        return new RessourceService.RessourceCommand(
                request.nom(),
                request.url(),
                request.type(),
                request.telechargeable()
        );
    }

    public RessourceResponse toResponse(RessourceService.RessourceResult result) {
        return new RessourceResponse(
                result.id(),
                result.nom(),
                result.url(),
                result.type(),
                result.telechargeable(),
                result.dateCreation(),
                result.chapitreId(),
                result.chapitreTitre()
        );
    }
}