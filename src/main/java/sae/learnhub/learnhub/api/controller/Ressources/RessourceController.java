package sae.learnhub.learnhub.api.controller.Ressources;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import sae.learnhub.learnhub.api.dto.Ressources_DTO.RessourceRequest;
import sae.learnhub.learnhub.api.dto.Ressources_DTO.RessourceResponse;
import sae.learnhub.learnhub.application.Service.RessourceService;

import java.util.List;

@RestController
@RequestMapping("/api/cours/{coursId}/chapitres/{chapitreId}/ressources")
@RequiredArgsConstructor
public class RessourceController {

    private final RessourceService ressourceService;

    @GetMapping
    public List<RessourceResponse> getAllRessourcesByChapitre(@PathVariable Long coursId,
            @PathVariable Long chapitreId,
            Authentication authentication) {
        boolean isProfesseur = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSEUR"));
        boolean isEtudiant = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ETUDIANT"));
        return ressourceService.findByChapitreId(coursId, chapitreId,
                isProfesseur ? authentication.getName() : null,
                isEtudiant ? authentication.getName() : null);
    }

    @PostMapping
    @PreAuthorize("hasRole('PROFESSEUR')")
    public RessourceResponse createRessource(@PathVariable Long coursId,
            @PathVariable Long chapitreId,
            @RequestBody RessourceRequest request,
            Authentication authentication) {
        return ressourceService.create(coursId, chapitreId, request, authentication.getName());
    }

    @PutMapping("/{ressourceId}")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public RessourceResponse updateRessource(@PathVariable Long coursId,
            @PathVariable Long chapitreId,
            @PathVariable Long ressourceId,
            @RequestBody RessourceRequest request,
            Authentication authentication) {
        return ressourceService.update(coursId, chapitreId, ressourceId, request, authentication.getName());
    }

    @DeleteMapping("/{ressourceId}")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public void deleteRessource(@PathVariable Long coursId,
            @PathVariable Long chapitreId,
            @PathVariable Long ressourceId,
            Authentication authentication) {
        ressourceService.delete(coursId, chapitreId, ressourceId, authentication.getName());
    }
}
