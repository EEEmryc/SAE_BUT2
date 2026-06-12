package sae.learnhub.learnhub.application.Cours_Service;

import sae.learnhub.learnhub.api.dto.Cours_DTO.CoursRequest;
import sae.learnhub.learnhub.api.dto.Cours_DTO.CoursResponse;

public final class CoursMapper {

    private CoursMapper() {
    }

    public static CoursService.CoursCommand toCommand(CoursRequest request) {
        return new CoursService.CoursCommand(
                request.titre(),
                request.description(),
                request.statut(),
                request.visibleCatalogue()
        );
    }

    public static CoursResponse toResponse(CoursService.CoursResult result) {
        return new CoursResponse(
                result.id(),
                result.titre(),
                result.description(),
                result.dateCreation(),
                result.statut(),
                result.visibleCatalogue(),
                result.profNom(),
                result.profPrenom(),
                result.profEmail()
        );
    }
}
