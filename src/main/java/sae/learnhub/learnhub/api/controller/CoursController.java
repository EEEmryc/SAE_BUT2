package sae.learnhub.learnhub.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sae.learnhub.learnhub.application.Service.CoursService;
import sae.learnhub.learnhub.domain.dto.CoursRequest;
import sae.learnhub.learnhub.domain.dto.CoursResponse;
import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/api/cours")
@RequiredArgsConstructor
public class CoursController {

    private final CoursService coursService;

    @GetMapping
    public List<CoursResponse> getAllCours(Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSEUR"))) {
            return coursService.findByProfEmail(authentication.getName());
        }
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ETUDIANT"))) {
            return coursService.findByEleveEmail(authentication.getName());
        }
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return coursService.findAllResponses();
        }
        return coursService.findAllResponses();
    }

    @PostMapping
    @PreAuthorize("hasRole('PROFESSEUR')")
    public CoursResponse creatCours(@RequestBody CoursRequest request, Authentication authentication) {
        return coursService.create(request, authentication.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public CoursResponse updateCours(@PathVariable Long id,
            @RequestBody CoursRequest request, Authentication authentication) {
        return coursService.update(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public void supprimerCours(@PathVariable Long id, Authentication authentication) {
        coursService.delete(id, authentication.getName());
    }
}