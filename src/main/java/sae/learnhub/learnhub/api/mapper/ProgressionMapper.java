package sae.elearning.api.mapper;

import org.springframework.stereotype.Component;
import sae.elearning.api.dto.ProgressionCoursResponse;
import sae.elearning.api.dto.ProgressionResponse;
import sae.elearning.application.service.ProgressionService;

@Component
public class ProgressionMapper {

    public ProgressionResponse toResponse(ProgressionService.ProgressionResult result) {
        return new ProgressionResponse(
                result.id(),
                result.statut(),
                result.pourcentage(),
                result.dateDebut(),
                result.dateMiseAJour(),
                result.dateFin(),
                result.eleveId(),
                result.eleveNom(),
                result.elevePrenom(),
                result.coursId(),
                result.coursTitre(),
                result.chapitreId(),
                result.chapitreTitre(),
                result.ressourceId(),
                result.ressourceNom()
        );
    }

    public ProgressionCoursResponse toCoursResponse(ProgressionService.ProgressionCoursResult result) {
        return new ProgressionCoursResponse(
                result.coursId(),
                result.coursTitre(),
                result.totalChapitres(),
                result.chapitresTermines(),
                result.pourcentageGlobal(),
                result.details().stream().map(this::toResponse).toList()
        );
    }
}