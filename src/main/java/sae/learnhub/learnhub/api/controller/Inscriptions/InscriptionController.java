package sae.elearning.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import sae.elearning.api.dto.CoursResponse;
import sae.elearning.api.dto.InscriptionRequest;
import sae.elearning.api.dto.InscriptionResponse;
import sae.elearning.api.dto.StatutRequest;
import sae.elearning.api.dto.UserResponse;
import sae.elearning.api.mapper.CoursMapper;
import sae.elearning.api.mapper.InscriptionMapper;
import sae.elearning.application.service.CoursService;
import sae.elearning.application.service.InscriptionService;

import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
@RequiredArgsConstructor
public class InscriptionController {

    private final InscriptionService inscriptionService;
    private final CoursService coursService;
    private final InscriptionMapper inscriptionMapper;
    private final CoursMapper coursMapper;

    @PostMapping("/cours/{coursId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InscriptionResponse> sInscrire(
            @PathVariable Long coursId,
            Authentication authentication) {
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inscriptionMapper.toResponse(
                        inscriptionService.inscrireEleve(coursId, authentication.getName())
                ));
    }

    @GetMapping("/mes-inscriptions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<InscriptionResponse>> getMesInscriptions(Authentication authentication) {
        return ResponseEntity.ok(
                inscriptionService.getInscriptionsParEleve(authentication.getName()).stream()
                        .map(inscriptionMapper::toResponse)
                        .toList()
        );
    }

    @GetMapping("/mes-cours-valides")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMesCoursValides(Authentication authentication) {
        boolean isProf = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSEUR"));
        
        if (isProf) {
            List<CoursResponse> cours = coursService.getCoursValidesParProf(authentication.getName()).stream()
                    .map(coursMapper::toResponse)
                    .toList();
            return ResponseEntity.ok(cours);
        }
        
        return ResponseEntity.ok(
                inscriptionService.getCoursValidesParEleve(authentication.getName()).stream()
                        .map(inscriptionMapper::toResponse)
                        .toList()
        );
    }

    @PostMapping("/cours/{coursId}/etudiants")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<InscriptionResponse> inscrireEtudiant(
            @PathVariable Long coursId,
            @RequestBody InscriptionRequest request,
            Authentication authentication) {
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inscriptionMapper.toResponse(
                        inscriptionService.inscrireEleveParProfesseur(
                                coursId, request.eleveId(), authentication.getName()
                        )
                ));
    }

    @GetMapping("/mes-cours/etudiants")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<List<InscriptionResponse>> getEtudiantsPourMesCours(Authentication authentication) {
        return ResponseEntity.ok(
                inscriptionService.getEtudiantsPourMesCours(authentication.getName()).stream()
                        .map(inscriptionMapper::toResponse)
                        .toList()
        );
    }

    @GetMapping("/cours/{coursId}/etudiants")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<List<InscriptionResponse>> getEtudiantsInscrits(
            @PathVariable Long coursId,
            Authentication authentication) {
        
        return ResponseEntity.ok(
                inscriptionService.getEtudiantsInscrits(coursId, authentication.getName()).stream()
                        .map(inscriptionMapper::toResponse)
                        .toList()
        );
    }

    @GetMapping("/etudiants")
    @PreAuthorize("hasAnyRole('PROFESSEUR', 'ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllStudents() {
        return ResponseEntity.ok(
                inscriptionService.getAllStudents().stream()
                        .map(inscriptionMapper::toUserResponse)
                        .toList()
        );
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR')")
    public ResponseEntity<InscriptionResponse> validerInscription(
            @PathVariable Long id,
            @RequestBody StatutRequest body,
            Authentication authentication) {
        
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String profEmail = isAdmin ? null : authentication.getName();
        
        return ResponseEntity.ok(
                inscriptionMapper.toResponse(
                        inscriptionService.changerStatutInscription(id, body.statut(), profEmail)
                )
        );
    }
}