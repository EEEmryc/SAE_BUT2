package sae.learnhub.learnhub.api.controller.Cours;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import sae.learnhub.learnhub.api.dto.Cours_DTO.CoursRequest;
import sae.learnhub.learnhub.api.dto.Cours_DTO.CoursResponse;
import sae.learnhub.learnhub.application.Cours_Service.CoursMapper;
import sae.learnhub.learnhub.application.Cours_Service.CoursService;

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
                        .toList()
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<CoursResponse> createCours(
            @Valid @RequestBody CoursRequest request,
            Authentication authentication) {
        
        return ResponseEntity.ok(
                CoursMapper.toResponse(
                        coursService.create(CoursMapper.toCommand(request), authentication.getName())
                )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<CoursResponse> updateCours(
            @PathVariable Long id,
            @Valid @RequestBody CoursRequest request,
            Authentication authentication) {
        
        return ResponseEntity.ok(
                CoursMapper.toResponse(
                        coursService.update(id, CoursMapper.toCommand(request), authentication.getName())
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<Void> supprimerCours(
            @PathVariable Long id,
            Authentication authentication) {
        
        coursService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}