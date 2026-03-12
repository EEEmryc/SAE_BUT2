package sae.learnhub.learnhub.api.controller.Progressions;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import sae.learnhub.learnhub.api.dto.Progressions_DTO.ProgressionCoursResponse;
import sae.learnhub.learnhub.api.dto.Progressions_DTO.ProgressionResponse;
import sae.learnhub.learnhub.application.Progressions_Service.ProgressionService;

import java.util.List;

@RestController
@RequestMapping("/api/progressions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ETUDIANT')")
@Tag(name = "Progressions", description = "Suivi de la progression des étudiants — une ligne par chapitre")
public class ProgressionController {

    private final ProgressionService progressionService;

    @PostMapping("/chapitres/{chapitreId}/commencer")
    @Operation(summary = "Commencer un chapitre", description = "Appelé quand l'étudiant ouvre un chapitre. Crée une ligne EN_COURS/0% si elle n'existe pas encore. "
            +
            "Sans effet si le chapitre est déjà TERMINE.")
    public ResponseEntity<ProgressionResponse> commencer(
            @PathVariable Long chapitreId,
            Authentication authentication) {
        return ResponseEntity.ok(progressionService.commencerChapitre(chapitreId, authentication.getName()));
    }

    @PostMapping("/chapitres/{chapitreId}/terminer")
    @Operation(summary = "Terminer un chapitre", description = "Appelé quand l'étudiant valide un chapitre. Met à jour le statut en TERMINE/100%.")
    public ResponseEntity<ProgressionResponse> terminer(
            @PathVariable Long chapitreId,
            Authentication authentication) {
        return ResponseEntity.ok(progressionService.terminerChapitre(chapitreId, authentication.getName()));
    }

    @GetMapping("/cours/{coursId}")
    @Operation(summary = "Progression globale pour un cours", description = "Retourne le pourcentage d'avancement global (chapitres TERMINE / total chapitres × 100) "
            +
            "ainsi que le détail chapitre par chapitre.")
    public ResponseEntity<ProgressionCoursResponse> getProgressionCours(
            @PathVariable Long coursId,
            Authentication authentication) {
        return ResponseEntity.ok(progressionService.getProgressionCours(coursId, authentication.getName()));
    }

    @GetMapping
    @Operation(summary = "Toutes mes progressions", description = "Retourne la progression globale de l'étudiant pour chaque cours qu'il a commencé.")
    public ResponseEntity<List<ProgressionCoursResponse>> getToutesProgressions(Authentication authentication) {
        return ResponseEntity.ok(progressionService.getToutesMesProgressions(authentication.getName()));
    }
}
