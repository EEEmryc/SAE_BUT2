package sae.learnhub.learnhub.api.mapper;

import sae.learnhub.learnhub.api.dto.Progressions_DTO.ProgressionCoursResponse;
import sae.learnhub.learnhub.api.dto.Progressions_DTO.ProgressionResponse;
import sae.learnhub.learnhub.application.Progressions_Service.ProgressionService;

public final class ProgressionMapper {

    private ProgressionMapper() {
    }

    public static ProgressionResponse toResponse(ProgressionService.ProgressionResult result) {
        return new ProgressionResponse(
                result.id(), result.statut(), result.pourcentage(),
                result.dateDebut(), result.dateMiseAJour(), result.dateFin(),
                result.eleveId(), result.eleveNom(), result.elevePrenom(),
                result.coursId(), result.coursTitre(),
                result.chapitreId(), result.chapitreTitre(),
                result.ressourceId(), result.ressourceNom());
    }

    public static ProgressionCoursResponse toCoursResponse(ProgressionService.ProgressionCoursResult result) {
        return new ProgressionCoursResponse(
                result.coursId(), result.coursTitre(), result.totalChapitres(),
                result.chapitresTermines(), result.pourcentageGlobal(),
                result.details().stream().map(ProgressionMapper::toResponse).toList());
    }
}
