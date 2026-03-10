package sae.learnhub.learnhub.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sae.learnhub.learnhub.application.Service.CoursService;
import sae.learnhub.learnhub.application.Service.InscriptionService;
import sae.learnhub.learnhub.domain.dto.CoursResponse;
import sae.learnhub.learnhub.domain.dto.InscriptionRequest;
import sae.learnhub.learnhub.domain.dto.StatutRequest;
import sae.learnhub.learnhub.domain.model.Inscription;
import sae.learnhub.learnhub.domain.model.User;

import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
@RequiredArgsConstructor
public class InscriptionController {

    private final InscriptionService inscriptionService;
    private final CoursService coursService;

    // =========================================================
    // Student endpoints
    // =========================================================

    /**
     * Student self-enrolls in a course.
     * POST /api/inscriptions/cours/{coursId}
     */
    @PostMapping("/cours/{coursId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Inscription> sInscrire(@PathVariable Long coursId,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inscriptionService.inscrireEleve(coursId, authentication.getName()));
    }

    /**
     * Student views their full enrollment history.
     * GET /api/inscriptions/mes-inscriptions
     */
    @GetMapping("/mes-inscriptions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Inscription>> getMesInscriptions(Authentication authentication) {
        return ResponseEntity.ok(inscriptionService.getInscriptionsParEleve(authentication.getName()));
    }

    /**
     * Returns validated content for the caller:
     * - PROFESSEUR → their own courses with statut=VALIDE (List<CoursResponse>)
     * - ETUDIANT → their enrollments with statut=VALIDE (List<Inscription>)
     * GET /api/inscriptions/mes-cours-valides
     */
    @GetMapping("/mes-cours-valides")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMesCoursValides(Authentication authentication) {
        boolean isProf = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSEUR"));
        if (isProf) {
            List<CoursResponse> cours = coursService.getCoursValidesParProf(authentication.getName());
            return ResponseEntity.ok(cours);
        }
        return ResponseEntity.ok(inscriptionService.getCoursValidesParEleve(authentication.getName()));
    }

    // =========================================================
    // Professor endpoints
    // =========================================================

    /**
     * Professor enrolls a specific student in one of their courses.
     * POST /api/inscriptions/cours/{coursId}/etudiants
     * Body: { "eleveId": 5 }
     */
    @PostMapping("/cours/{coursId}/etudiants")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<Inscription> inscrireEtudiant(@PathVariable Long coursId,
            @RequestBody InscriptionRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inscriptionService.inscrireEleveParProfesseur(
                        coursId, request.getEleveId(), authentication.getName()));
    }

    /**
     * Professor views all students enrolled across ALL their courses.
     * GET /api/inscriptions/mes-cours/etudiants
     */
    @GetMapping("/mes-cours/etudiants")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<List<Inscription>> getEtudiantsPourMesCours(Authentication authentication) {
        return ResponseEntity.ok(
                inscriptionService.getEtudiantsPourMesCours(authentication.getName()));
    }

    /**
     * Professor views all students enrolled in one of their courses.
     * GET /api/inscriptions/cours/{coursId}/etudiants
     */
    @GetMapping("/cours/{coursId}/etudiants")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<List<Inscription>> getEtudiantsInscrits(@PathVariable Long coursId,
            Authentication authentication) {
        return ResponseEntity.ok(
                inscriptionService.getEtudiantsInscrits(coursId, authentication.getName()));
    }

    /**
     * Professor (or Admin) retrieves the full list of students to select from.
     * GET /api/inscriptions/etudiants
     */
    @GetMapping("/etudiants")
    @PreAuthorize("hasAnyRole('PROFESSEUR', 'ADMIN')")
    public ResponseEntity<List<User>> getAllStudents() {
        return ResponseEntity.ok(inscriptionService.getAllStudents());
    }

    // =========================================================
    // Professor / Admin endpoints
    // =========================================================

    /**
     * Approve or reject an enrollment request.
     * PATCH /api/inscriptions/{id}/statut
     * Body: { "statut": "VALIDE" | "REFUSE" }
     */
    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('PROFESSEUR', 'ADMIN')")
    public ResponseEntity<Inscription> validerInscription(
            @PathVariable Long id,
            @RequestBody StatutRequest body,
            Authentication authentication) {
        return ResponseEntity.ok(
                inscriptionService.changerStatutInscription(id, body.getStatut(), authentication.getName()));
    }
}
