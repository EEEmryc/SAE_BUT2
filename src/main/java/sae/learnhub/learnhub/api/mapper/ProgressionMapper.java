package sae.learnhub.learnhub.api.mapper;

import sae.learnhub.learnhub.api.dto.progression.ProgressionCoursResponse;
import sae.learnhub.learnhub.api.dto.progression.ProgressionResponse;
import sae.learnhub.learnhub.api.dto.progression.ProfessorStudentProgressResponse;
import sae.learnhub.learnhub.application.progression.ProgressionService;

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
                result.coursId(), result.coursTitre(), result.profNom(), result.profPrenom(),
                result.totalChapitres(), result.chapitresTermines(), result.totalRessources(),
                result.pourcentageGlobal(),
                result.details().stream().map(ProgressionMapper::toResponse).toList());
    }

    public static ProfessorStudentProgressResponse toProfessorResponse(
            ProgressionService.ProfessorStudentProgressResult result
    ) {
        return new ProfessorStudentProgressResponse(
                result.inscriptionId(),
                result.eleveId(),
                result.eleveNom(),
                result.elevePrenom(),
                result.eleveEmail(),
                result.coursId(),
                result.coursTitre(),
                result.chapitresTermines(),
                result.totalChapitres(),
                result.pourcentage(),
                result.derniereActivite());
    }
}
