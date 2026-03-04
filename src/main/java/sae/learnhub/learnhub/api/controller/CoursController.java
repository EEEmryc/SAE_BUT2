package sae.learnhub.learnhub.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
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
    @PreAuthorize("hasAnyAuthority('PROF')")
    public CoursResponse creatCours(@RequestBody CoursRequest request, Authentication authentication) {
        try {
            return coursService.create(request, authentication.getName());
        } catch (ResponseStatusException ex) {
            throw ex;
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PROF')")
    public CoursResponse updateCours(@PathVariable Long id,
                               @RequestBody CoursRequest request, Authentication authentication) {
        try {
            return coursService.update(id, request, authentication.getName());
        } catch (ResponseStatusException ex) {
            throw ex;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PROF')")
    public void supprimerCours(@PathVariable Long id, Authentication authentication) {
        try {
            coursService.delete(id, authentication.getName());
        } catch (ResponseStatusException ex) {
            throw ex;
        }
    }
}