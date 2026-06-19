package sae.learnhub.learnhub.api.controller.Cours;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import sae.learnhub.learnhub.api.dto.cours.CoursRequest;
import sae.learnhub.learnhub.api.dto.cours.CoursResponse;
import sae.learnhub.learnhub.api.dto.cours.CatalogCourseResponse;
import sae.learnhub.learnhub.api.dto.cours.CourseSummaryResponse;
import sae.learnhub.learnhub.api.mapper.CoursMapper;
import sae.learnhub.learnhub.application.cours.CoursService;
import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.application.port.ResourceFileStorage;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/cours")
@RequiredArgsConstructor
public class CoursController {

    private final CoursService coursService;

    @GetMapping
    public ResponseEntity<List<CoursResponse>> getAllCours(Authentication authentication) {
        List<CoursService.CoursResult> results;

        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSEUR"))) {
            results = coursService.findByProfEmail(authentication.getName());
        } else if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ETUDIANT"))) {
            results = coursService.findByEleveEmail(authentication.getName());
        } else {
            results = coursService.findAll();
        }

        return ResponseEntity.ok(
                results.stream()
                        .map(CoursMapper::toResponse)
                        .toList());
    }

    @GetMapping("/{id:\\d+}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CoursResponse> getCours(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(CoursMapper.toResponse(
                coursService.findAccessibleById(
                        id,
                        authentication.getName(),
                        hasAuthority(authentication, "ROLE_PROFESSEUR"),
                        hasAuthority(authentication, "ROLE_ETUDIANT"),
                        hasAuthority(authentication, "ROLE_ADMIN"))));
    }

    @GetMapping("/catalogue")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<List<CatalogCourseResponse>> getCatalogue(
            Authentication authentication) {
        return ResponseEntity.ok(
                coursService.getCatalogue(authentication.getName()).stream()
                        .map(course -> new CatalogCourseResponse(
                                course.id(),
                                course.titre(),
                                course.description(),
                                course.statut(),
                                course.profNom(),
                                course.profPrenom(),
                                course.profEmail(),
                                course.nombreChapitres(),
                                course.nombreRessources(),
                                course.statutInscription()))
                        .toList());
    }

    @GetMapping("/{id}/summary")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<CourseSummaryResponse> getCourseSummary(
            @PathVariable Long id,
            Authentication authentication) {
        CoursService.CourseSummaryResult summary =
                coursService.getProfessorSummary(id, authentication.getName());
        return ResponseEntity.ok(new CourseSummaryResponse(
                summary.students(),
                summary.chapters(),
                summary.resources(),
                summary.averageProgress()));
    }

    @PostMapping
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<CoursResponse> createCours(
            @Valid @RequestBody CoursRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                CoursMapper.toResponse(
                        coursService.create(CoursMapper.toCommand(request), authentication.getName())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<CoursResponse> updateCours(
            @PathVariable Long id,
            @Valid @RequestBody CoursRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                CoursMapper.toResponse(
                        coursService.update(id, CoursMapper.toCommand(request), authentication.getName())));
    }

    @PostMapping(value = "/{id}/fichier-principal", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<CoursResponse> uploadMainFile(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(CoursMapper.toResponse(
                    coursService.uploadMainFile(
                            id,
                            new ResourceFileStorage.FileUpload(
                                    file.getOriginalFilename(),
                                    file.getContentType() == null
                                            ? MediaType.APPLICATION_OCTET_STREAM_VALUE
                                            : file.getContentType(),
                                    file.getSize(),
                                    file.getInputStream()),
                            authentication.getName())));
        } catch (IOException exception) {
            throw new BusinessRuleException("Le fichier n'a pas pu être lu");
        }
    }

    @DeleteMapping("/{id}/fichier-principal")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<CoursResponse> deleteMainFile(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(CoursMapper.toResponse(
                coursService.deleteMainFile(id, authentication.getName())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<Void> supprimerCours(
            @PathVariable Long id,
            Authentication authentication) {

        coursService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().stream()
                .anyMatch(candidate -> candidate.getAuthority().equals(authority));
    }
}
