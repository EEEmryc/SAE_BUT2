package sae.learnhub.learnhub.api.controller.Chapitres;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import sae.learnhub.learnhub.api.dto.Chapitre_DTO.ChapitreRequest;
import sae.learnhub.learnhub.api.dto.Chapitre_DTO.ChapitreResponse;
import sae.learnhub.learnhub.api.mapper.ChapitreMapper;
import sae.learnhub.learnhub.application.Chapitre_Service.ChapitreService;

import java.util.List;

@RestController
@RequestMapping("/api/cours/{coursId}/chapitres")
@RequiredArgsConstructor
public class ChapitreController {

        private final ChapitreService chapitreService;

        @GetMapping
        public ResponseEntity<List<ChapitreResponse>> getAllChapitresByCours(
                        @PathVariable Long coursId,
                        Authentication authentication) {

                boolean isProfesseur = authentication != null && authentication.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSEUR"));
                boolean isEtudiant = authentication != null && authentication.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_ETUDIANT"));

                return ResponseEntity.ok(
                                chapitreService.findByCoursId(
                                                coursId,
                                                isProfesseur ? authentication.getName() : null,
                                                isEtudiant ? authentication.getName() : null).stream()
                                                .map(ChapitreMapper::toResponse).toList());
        }

        @PostMapping
        @PreAuthorize("hasRole('PROFESSEUR')")
        public ResponseEntity<ChapitreResponse> createChapitre(
                        @PathVariable Long coursId,
                        @Valid @RequestBody ChapitreRequest request,
                        Authentication authentication) {

                return ResponseEntity.ok(
                                ChapitreMapper.toResponse(
                                                chapitreService.create(coursId, ChapitreMapper.toCommand(request),
                                                                authentication.getName())));
        }

        @PutMapping("/{chapitreId}")
        @PreAuthorize("hasRole('PROFESSEUR')")
        public ResponseEntity<ChapitreResponse> updateChapitre(
                        @PathVariable Long coursId,
                        @PathVariable Long chapitreId,
                        @Valid @RequestBody ChapitreRequest request,
                        Authentication authentication) {

                return ResponseEntity.ok(
                                ChapitreMapper.toResponse(
                                                chapitreService.update(coursId, chapitreId,
                                                                ChapitreMapper.toCommand(request),
                                                                authentication.getName())));
        }

        @DeleteMapping("/{chapitreId}")
        @PreAuthorize("hasRole('PROFESSEUR')")
        public ResponseEntity<Void> deleteChapitre(
                        @PathVariable Long coursId,
                        @PathVariable Long chapitreId,
                        Authentication authentication) {

                chapitreService.delete(coursId, chapitreId, authentication.getName());
                return ResponseEntity.noContent().build();
        }
}