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
    public List<CoursResponse> getAllCours() {
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