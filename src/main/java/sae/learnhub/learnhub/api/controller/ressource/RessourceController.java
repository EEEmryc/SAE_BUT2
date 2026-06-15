package sae.learnhub.learnhub.api.controller.ressource;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import sae.learnhub.learnhub.api.dto.ressource.RessourceRequest;
import sae.learnhub.learnhub.api.dto.ressource.RessourceResponse;
import sae.learnhub.learnhub.api.mapper.RessourceMapper;
import sae.learnhub.learnhub.application.ressource.RessourceService;
import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.application.port.ResourceFileStorage;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/cours/{coursId}/chapitres/{chapitreId}/ressources")
@RequiredArgsConstructor
public class RessourceController {

        private final RessourceService ressourceService;

        @GetMapping
        @PreAuthorize("isAuthenticated()")
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

        @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @PreAuthorize("hasRole('PROFESSEUR')")
        public ResponseEntity<RessourceResponse> uploadRessource(
                        @PathVariable Long coursId,
                        @PathVariable Long chapitreId,
                        @RequestPart("file") MultipartFile file,
                        @RequestParam(required = false) String nom,
                        @RequestParam(defaultValue = "true") Boolean telechargeable,
                        Authentication authentication) {
                try {
                        ResourceFileStorage.FileUpload upload = new ResourceFileStorage.FileUpload(
                                        file.getOriginalFilename(),
                                        file.getContentType() == null
                                                        ? MediaType.APPLICATION_OCTET_STREAM_VALUE
                                                        : file.getContentType(),
                                        file.getSize(),
                                        file.getInputStream());
                        return ResponseEntity.ok(RessourceMapper.toResponse(
                                        ressourceService.upload(
                                                        coursId,
                                                        chapitreId,
                                                        nom,
                                                        telechargeable,
                                                        upload,
                                                        authentication.getName())));
                } catch (IOException exception) {
                        throw new BusinessRuleException("Le fichier n'a pas pu être lu");
                }
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
