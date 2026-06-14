package sae.learnhub.learnhub.api.controller.Inscriptions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import sae.learnhub.learnhub.api.dto.Cours_DTO.CoursResponse;
import sae.learnhub.learnhub.api.dto.Inscriptions_DTO.InscriptionRequest;
import sae.learnhub.learnhub.api.dto.Inscriptions_DTO.InscriptionResponse;
import sae.learnhub.learnhub.api.dto.Stat_Refresh_DTO.StatutRequest;
import sae.learnhub.learnhub.api.dto.User_DTO.UserResponse;
import sae.learnhub.learnhub.api.mapper.CoursMapper;
import sae.learnhub.learnhub.api.mapper.InscriptionMapper;
import sae.learnhub.learnhub.application.Cours_Service.CoursService;
import sae.learnhub.learnhub.application.Inscriptions_Service.InscriptionService;

import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
@RequiredArgsConstructor
public class InscriptionController {

        private final InscriptionService inscriptionService;
        private final CoursService coursService;

        @PostMapping("/cours/{coursId}")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<InscriptionResponse> sInscrire(
                        @PathVariable Long coursId,
                        Authentication authentication) {

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(InscriptionMapper.toResponse(
                                                inscriptionService.inscrireEleve(coursId, authentication.getName())));
        }

        @GetMapping("/mes-inscriptions")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<List<InscriptionResponse>> getMesInscriptions(Authentication authentication) {
                return ResponseEntity.ok(
                                inscriptionService.getInscriptionsParEleve(authentication.getName()).stream()
                                                .map(InscriptionMapper::toResponse)
                                                .toList());
        }

        @GetMapping("/mes-cours-valides")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<?> getMesCoursValides(Authentication authentication) {
                boolean isProf = authentication.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSEUR"));

                if (isProf) {
                        List<CoursResponse> cours = coursService.getCoursValidesParProf(authentication.getName())
                                        .stream()
                                        .map(CoursMapper::toResponse)
                                        .toList();
                        return ResponseEntity.ok(cours);
                }

                return ResponseEntity.ok(
                                inscriptionService.getCoursValidesParEleve(authentication.getName()).stream()
                                                .map(InscriptionMapper::toResponse)
                                                .toList());
        }

        @PostMapping("/cours/{coursId}/etudiants")
        @PreAuthorize("hasRole('PROFESSEUR')")
        public ResponseEntity<InscriptionResponse> inscrireEtudiant(
                        @PathVariable Long coursId,
                        @RequestBody InscriptionRequest request,
                        Authentication authentication) {

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(InscriptionMapper.toResponse(
                                                inscriptionService.inscrireEleveParProfesseur(
                                                                coursId, request.eleveId(), authentication.getName())));
        }

        @GetMapping("/mes-cours/etudiants")
        @PreAuthorize("hasRole('PROFESSEUR')")
        public ResponseEntity<List<InscriptionResponse>> getEtudiantsPourMesCours(Authentication authentication) {
                return ResponseEntity.ok(
                                inscriptionService.getEtudiantsPourMesCours(authentication.getName()).stream()
                                                .map(InscriptionMapper::toResponse)
                                                .toList());
        }

        @GetMapping("/cours/{coursId}/etudiants")
        @PreAuthorize("hasRole('PROFESSEUR')")
        public ResponseEntity<List<InscriptionResponse>> getEtudiantsInscrits(
                        @PathVariable Long coursId,
                        Authentication authentication) {

                return ResponseEntity.ok(
                                inscriptionService.getEtudiantsInscrits(coursId, authentication.getName()).stream()
                                                .map(InscriptionMapper::toResponse)
                                                .toList());
        }

        @GetMapping("/etudiants")
        @PreAuthorize("hasAnyRole('PROFESSEUR', 'ADMIN')")
        public ResponseEntity<List<UserResponse>> getAllStudents() {
                return ResponseEntity.ok(
                                inscriptionService.getAllStudents().stream()
                                                .map(InscriptionMapper::toUserResponse)
                                                .toList());
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
                                InscriptionMapper.toResponse(
                                                inscriptionService.changerStatutInscription(id, body.statut(),
                                                                profEmail)));
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('PROFESSEUR')")
        public ResponseEntity<Void> retirerEtudiant(
                        @PathVariable Long id,
                        Authentication authentication) {
                inscriptionService.retirerEtudiant(id, authentication.getName());
                return ResponseEntity.noContent().build();
        }
}
