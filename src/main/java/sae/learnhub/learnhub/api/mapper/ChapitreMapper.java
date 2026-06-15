package sae.learnhub.learnhub.api.mapper;

import sae.learnhub.learnhub.api.dto.chapitre.ChapitreRequest;
import sae.learnhub.learnhub.api.dto.chapitre.ChapitreResponse;
import sae.learnhub.learnhub.application.chapitre.ChapitreService;
import sae.learnhub.learnhub.domain.model.Chapitre;

public final class ChapitreMapper {

    private ChapitreMapper() {
    }

    public static ChapitreService.ChapitreCommand toCommand(ChapitreRequest request) {
        return new ChapitreService.ChapitreCommand(
                request.titre(),
                request.contenu(),
                request.ordre());
    }

    public static ChapitreResponse toResponse(ChapitreService.ChapitreResult result) {
        return new ChapitreResponse(
                result.id(),
                result.titre(),
                result.contenu(),
                result.ordre(),
                result.dateCreation(),
                result.fichierPrincipalNom(),
                result.fichierPrincipalUrl(),
                result.fichierPrincipalType(),
                result.fichierPrincipalTailleOctets(),
                result.coursId(),
                result.coursTitre());
    }

    public static ChapitreResponse toResponse(Chapitre chapitre) {
        return new ChapitreResponse(
                chapitre.getId(),
                chapitre.getTitre(),
                chapitre.getContenu(),
                chapitre.getOrdre(),
                chapitre.getDateCreation(),
                chapitre.getFichierPrincipalNom(),
                chapitre.getFichierPrincipalUrl(),
                chapitre.getFichierPrincipalType(),
                chapitre.getFichierPrincipalTailleOctets(),
                chapitre.getCours().getId(),
                chapitre.getCours().getTitre());
    }
}
