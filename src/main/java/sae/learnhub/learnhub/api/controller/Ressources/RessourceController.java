package sae.learnhub.learnhub.api.controller.Ressources;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import sae.learnhub.learnhub.api.dto.Ressources_DTO.RessourceRequest;
import sae.learnhub.learnhub.api.dto.Ressources_DTO.RessourceResponse;
import sae.learnhub.learnhub.api.mapper.RessourceMapper;
import sae.learnhub.learnhub.application.Ressource_Service.RessourceService;

import java.util.List;

@RestController
@RequestMapping("/api/cours/{coursId}/chapitres/{chapitreId}/ressources")
@RequiredArgsConstructor
public class RessourceController {

        private final RessourceService ressourceService;

        @GetMapping
        public ResponseEntity<List<RessourceResponse>> getAllRessourcesByChapitre(
                        @PathVariable Long coursId,
                        @PathVariable Long chapitreId,
                        Authentication authentication) {

                boolean isProfesseur = authentication != null && authentication.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSEUR"));
                boolean isEtudiant = authentication != null && authentication.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_ETUDIANT"));

                return ResponseEntity.ok(
                                ressourceService.findByChapitreId(
                                                coursId,
                                                chapitreId,
                                                isProfesseur ? authentication.getName() : null,
                                                isEtudiant ? authentication.getName() : null).stream()
                                                .map(RessourceMapper::toResponse).toList());
        }

        @PostMapping
        @PreAuthorize("hasRole('PROFESSEUR')")
        public ResponseEntity<RessourceResponse> createRessource(
                        @PathVariable Long coursId,
                        @PathVariable Long chapitreId,
                        @Valid @RequestBody RessourceRequest request,
                        Authentication authentication) {

                return ResponseEntity.ok(
                                RessourceMapper.toResponse(
                                                ressourceService.create(coursId, chapitreId,
                                                                RessourceMapper.toCommand(request),
                                                                authentication.getName())));
        }

        @PutMapping("/{ressourceId}")
        @PreAuthorize("hasRole('PROFESSEUR')")
        public ResponseEntity<RessourceResponse> updateRessource(
                        @PathVariable Long coursId,
                        @PathVariable Long chapitreId,
                        @PathVariable Long ressourceId,
                        @Valid @RequestBody RessourceRequest request,
                        Authentication authentication) {

                return ResponseEntity.ok(
                                RessourceMapper.toResponse(
                                                ressourceService.update(coursId, chapitreId, ressourceId,
                                                                RessourceMapper.toCommand(request),
                                                                authentication.getName())));
        }

        @DeleteMapping("/{ressourceId}")
        @PreAuthorize("hasRole('PROFESSEUR')")
        public ResponseEntity<Void> deleteRessource(
                        @PathVariable Long coursId,
                        @PathVariable Long chapitreId,
                        @PathVariable Long ressourceId,
                        Authentication authentication) {

                ressourceService.delete(coursId, chapitreId, ressourceId, authentication.getName());
                return ResponseEntity.noContent().build();
        }
}