package sae.elearning.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import sae.elearning.api.dto.CoursRequest;
import sae.elearning.api.dto.CoursResponse;
import sae.elearning.api.mapper.CoursMapper;
import sae.elearning.application.service.CoursService;

import java.util.List;

@RestController
@RequestMapping("/api/cours")
@RequiredArgsConstructor
public class CoursController {

    private final CoursService coursService;
    private final CoursMapper coursMapper;

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
                        .map(coursMapper::toResponse)
                        .toList()
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<CoursResponse> createCours(
            @Valid @RequestBody CoursRequest request,
            Authentication authentication) {
        
        return ResponseEntity.ok(
                coursMapper.toResponse(
                        coursService.create(coursMapper.toCommand(request), authentication.getName())
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
                coursMapper.toResponse(
                        coursService.update(id, coursMapper.toCommand(request), authentication.getName())
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