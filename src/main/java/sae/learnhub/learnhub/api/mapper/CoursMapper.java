package sae.elearning.api.mapper;

import org.springframework.stereotype.Component;
import sae.elearning.api.dto.CoursRequest;
import sae.elearning.api.dto.CoursResponse;
import sae.elearning.application.service.CoursService;

@Component
public class CoursMapper {

    public CoursService.CoursCommand toCommand(CoursRequest request) {
        return new CoursService.CoursCommand(
                request.titre(),
                request.description(),
                request.statut(),
                request.visibleCatalogue()
        );
    }

    public CoursResponse toResponse(CoursService.CoursResult result) {
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