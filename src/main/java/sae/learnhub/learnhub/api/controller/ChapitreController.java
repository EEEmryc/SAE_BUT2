package sae.learnhub.learnhub.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sae.learnhub.learnhub.application.Service.ChapitreService;
import sae.learnhub.learnhub.domain.dto.ChapitreRequest;
import sae.learnhub.learnhub.domain.dto.ChapitreResponse;
import java.util.List;

@RestController
@RequestMapping("/api/cours/{coursId}/chapitres")
@RequiredArgsConstructor
public class ChapitreController {

    private final ChapitreService chapitreService;

    @GetMapping
    public List<ChapitreResponse> getAllChapitresByCours(@PathVariable Long coursId,
            Authentication authentication) {
        boolean isProfesseur = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSEUR"));
        boolean isEtudiant = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ETUDIANT"));
        return chapitreService.findByCoursId(
                coursId,
                isProfesseur ? authentication.getName() : null,
                isEtudiant ? authentication.getName() : null);
    }

    @PostMapping
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ChapitreResponse createChapitre(@PathVariable Long coursId,
            @RequestBody ChapitreRequest request,
            Authentication authentication) {
        return chapitreService.create(coursId, request, authentication.getName());
    }

    @PutMapping("/{chapitreId}")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ChapitreResponse updateChapitre(@PathVariable Long coursId,
            @PathVariable Long chapitreId,
            @RequestBody ChapitreRequest request,
            Authentication authentication) {
        return chapitreService.update(coursId, chapitreId, request, authentication.getName());
    }

    @DeleteMapping("/{chapitreId}")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public void deleteChapitre(@PathVariable Long coursId,
            @PathVariable Long chapitreId,
            Authentication authentication) {
        chapitreService.delete(coursId, chapitreId, authentication.getName());
    }
}
