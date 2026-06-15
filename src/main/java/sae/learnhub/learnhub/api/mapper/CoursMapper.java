package sae.learnhub.learnhub.api.mapper;

import sae.learnhub.learnhub.api.dto.cours.CoursRequest;
import sae.learnhub.learnhub.api.dto.cours.CoursResponse;
import sae.learnhub.learnhub.application.cours.CoursService;

public final class CoursMapper {

    private CoursMapper() {
    }

    public static CoursService.CoursCommand toCommand(CoursRequest request) {
        return new CoursService.CoursCommand(
                request.titre(),
                request.description(),
                request.statut(),
                request.visibleCatalogue());
    }

    public static CoursResponse toResponse(CoursService.CoursResult result) {
        return new CoursResponse(
                result.id(),
                result.titre(),
                result.description(),
                result.dateCreation(),
                result.statut(),
                result.visibleCatalogue(),
                result.fichierPrincipalNom(),
                result.fichierPrincipalUrl(),
                result.fichierPrincipalType(),
                result.fichierPrincipalTailleOctets(),
                result.profNom(),
                result.profPrenom(),
                result.profEmail());
    }
}
