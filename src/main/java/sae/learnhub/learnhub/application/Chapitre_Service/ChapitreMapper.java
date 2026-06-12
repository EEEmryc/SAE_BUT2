package sae.learnhub.learnhub.application.Chapitre_Service;

import sae.learnhub.learnhub.api.dto.Chapitre_DTO.ChapitreRequest;
import sae.learnhub.learnhub.api.dto.Chapitre_DTO.ChapitreResponse;
import sae.learnhub.learnhub.domain.model.Chapitre;

public class ChapitreMapper {

    private ChapitreMapper() {
    }

    public static ChapitreService.ChapitreCommand toCommand(ChapitreRequest request) {
        return new ChapitreService.ChapitreCommand(
                request.titre(),
                request.contenu(),
                request.ordre()
        );
    }

    public static ChapitreResponse toResponse(ChapitreService.ChapitreResult result) {
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

    public static ChapitreResponse toResponse(Chapitre chapitre) {
        return new ChapitreResponse(
                chapitre.getId(),
                chapitre.getTitre(),
                chapitre.getContenu(),
                chapitre.getOrdre(),
                chapitre.getDateCreation(),
                chapitre.getCours().getId(),
                chapitre.getCours().getTitre());
    }
}
