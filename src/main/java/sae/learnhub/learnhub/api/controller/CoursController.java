package sae.learnhub.learnhub.api.controller;

import lombok.RequiredArgsConstructor;
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
    public CoursResponse creatCours(@RequestBody CoursRequest request, Authentication authentication) {
        
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PROF"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réservé aux professeurs");
        }
        
        return coursService.create(request, authentication.getName());
    }

    @PutMapping("/{id}")
    public CoursResponse updateCours(@PathVariable Long id,
                               @RequestBody CoursRequest request, Authentication authentication) {
        
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PROF"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réservé aux professeurs");
        }
        
        return coursService.update(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    public void supprimerCours(@PathVariable Long id, Authentication authentication) {
       
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PROF"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réservé aux professeurs");
        }
        
        coursService.delete(id, authentication.getName());
    }
}