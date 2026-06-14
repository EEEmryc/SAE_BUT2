package sae.learnhub.learnhub.api.controller.Chapitres;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import sae.learnhub.learnhub.api.dto.Chapitre_DTO.ChapitreRequest;
import sae.learnhub.learnhub.api.dto.Chapitre_DTO.ChapitreResponse;
import sae.learnhub.learnhub.api.mapper.ChapitreMapper;
import sae.learnhub.learnhub.application.Chapitre_Service.ChapitreService;
import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.application.port.ResourceFileStorage;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/cours/{coursId}/chapitres")
@RequiredArgsConstructor
public class ChapitreController {

        private final ChapitreService chapitreService;

        @GetMapping
        @PreAuthorize("isAuthenticated()")
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

        @PostMapping(
                        value = "/{chapitreId}/fichier-principal",
                        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @PreAuthorize("hasRole('PROFESSEUR')")
        public ResponseEntity<ChapitreResponse> uploadMainFile(
                        @PathVariable Long coursId,
                        @PathVariable Long chapitreId,
                        @RequestPart("file") MultipartFile file,
                        Authentication authentication) {
                try {
                        ResourceFileStorage.FileUpload upload = new ResourceFileStorage.FileUpload(
                                        file.getOriginalFilename(),
                                        file.getContentType() == null
                                                        ? MediaType.APPLICATION_OCTET_STREAM_VALUE
                                                        : file.getContentType(),
                                        file.getSize(),
                                        file.getInputStream());
                        return ResponseEntity.ok(ChapitreMapper.toResponse(
                                        chapitreService.uploadMainFile(
                                                        coursId,
                                                        chapitreId,
                                                        upload,
                                                        authentication.getName())));
                } catch (IOException exception) {
                        throw new BusinessRuleException("Le fichier n'a pas pu etre lu");
                }
        }

        @DeleteMapping("/{chapitreId}/fichier-principal")
        @PreAuthorize("hasRole('PROFESSEUR')")
        public ResponseEntity<ChapitreResponse> deleteMainFile(
                        @PathVariable Long coursId,
                        @PathVariable Long chapitreId,
                        Authentication authentication) {
                return ResponseEntity.ok(ChapitreMapper.toResponse(
                                chapitreService.deleteMainFile(
                                                coursId,
                                                chapitreId,
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
