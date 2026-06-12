package sae.learnhub.learnhub.api.mapper;

import sae.learnhub.learnhub.api.dto.Ressources_DTO.RessourceRequest;
import sae.learnhub.learnhub.api.dto.Ressources_DTO.RessourceResponse;
import sae.learnhub.learnhub.application.Ressource_Service.RessourceService;
import sae.learnhub.learnhub.domain.model.Ressource;

public final class RessourceMapper {

    private RessourceMapper() {
    }

    public static RessourceService.RessourceCommand toCommand(RessourceRequest request) {
        return new RessourceService.RessourceCommand(
                request.nom(),
                request.url(),
                request.type(),
                request.telechargeable());
    }

    public static RessourceResponse toResponse(RessourceService.RessourceResult result) {
        return new RessourceResponse(
                result.id(),
                result.nom(),
                result.url(),
                result.type(),
                result.telechargeable(),
                result.dateCreation(),
                result.chapitreId(),
                result.chapitreTitre());
    }

    public static RessourceResponse toResponse(Ressource ressource) {
        return new RessourceResponse(
                ressource.getId(),
                ressource.getNom(),
                ressource.getUrl(),
                ressource.getType(),
                ressource.getTelechargeable(),
                ressource.getDateCreation(),
                ressource.getChapitre().getId(),
                ressource.getChapitre().getTitre());
    }
}
