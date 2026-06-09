package sae.elearning.api.mapper;

import org.springframework.stereotype.Component;
import sae.elearning.api.dto.ChapitreRequest;
import sae.elearning.api.dto.ChapitreResponse;
import sae.elearning.application.service.ChapitreService;

@Component
public class ChapitreMapper {

    public ChapitreService.ChapitreCommand toCommand(ChapitreRequest request) {
        return new ChapitreService.ChapitreCommand(
                request.titre(),
                request.contenu(),
                request.ordre()
        );
    }

    public ChapitreResponse toResponse(ChapitreService.ChapitreResult result) {
        return new ChapitreResponse(
                result.id(),
                result.titre(),
                result.contenu(),
                result.ordre(),
                result.dateCreation(),
                result.coursId(),
                result.coursTitre()
        );
    }
}