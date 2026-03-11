package sae.learnhub.learnhub.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import sae.learnhub.learnhub.api.dto.CoursResponse;
import sae.learnhub.learnhub.api.dto.InscriptionRequest;
import sae.learnhub.learnhub.api.dto.StatutRequest;
import sae.learnhub.learnhub.application.Service.CoursService;
import sae.learnhub.learnhub.application.Service.InscriptionService;
import sae.learnhub.learnhub.domain.model.Inscription;
import sae.learnhub.learnhub.domain.model.User;

import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
@RequiredArgsConstructor
public class InscriptionController {

    private final InscriptionService inscriptionService;
    private final CoursService coursService;

    @PostMapping("/cours/{coursId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Inscription> sInscrire(@PathVariable Long coursId,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inscriptionService.inscrireEleve(coursId, authentication.getName()));
    }

    @GetMapping("/mes-inscriptions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Inscription>> getMesInscriptions(Authentication authentication) {
        return ResponseEntity.ok(inscriptionService.getInscriptionsParEleve(authentication.getName()));
    }

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

    @PostMapping("/cours/{coursId}/etudiants")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<Inscription> inscrireEtudiant(@PathVariable Long coursId,
            @RequestBody InscriptionRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inscriptionService.inscrireEleveParProfesseur(
                        coursId, request.getEleveId(), authentication.getName()));
    }

    @GetMapping("/mes-cours/etudiants")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<List<Inscription>> getEtudiantsPourMesCours(Authentication authentication) {
        return ResponseEntity.ok(
                inscriptionService.getEtudiantsPourMesCours(authentication.getName()));
    }

    @GetMapping("/cours/{coursId}/etudiants")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<List<Inscription>> getEtudiantsInscrits(@PathVariable Long coursId,
            Authentication authentication) {
        return ResponseEntity.ok(
                inscriptionService.getEtudiantsInscrits(coursId, authentication.getName()));
    }

    @GetMapping("/etudiants")
    @PreAuthorize("hasAnyRole('PROFESSEUR', 'ADMIN')")
    public ResponseEntity<List<User>> getAllStudents() {
        return ResponseEntity.ok(inscriptionService.getAllStudents());
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Inscription> validerInscription(
            @PathVariable Long id,
            @RequestBody StatutRequest body) {
        return ResponseEntity.ok(
                inscriptionService.changerStatutInscription(id, body.getStatut()));
    }
}
