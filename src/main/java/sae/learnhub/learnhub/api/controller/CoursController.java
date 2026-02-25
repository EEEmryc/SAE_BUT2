package sae.learnhub.learnhub.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sae.learnhub.learnhub.application.Service.CoursService;
import sae.learnhub.learnhub.domain.model.Cours;
import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/api/cours")
@RequiredArgsConstructor
public class CoursController {

    private final CoursService coursService;

    @GetMapping
    public List<Cours> getAllCours() {
        return coursService.findAll();
    }
    
    @PostMapping
    @PreAuthorize("hasAnyAuthority('PROF')")
    public Cours creatCours(@RequestBody Cours cours , Authentication authentication) {
        return coursService.create(cours, authentication.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PROF')")
    public Cours updateCours(@PathVariable Long id,
                        @RequestBody Cours cours, Authentication authentication) {
        return coursService.update(id, cours, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PROF')")
    public void supprimerCours(@PathVariable Long id, Authentication authentication) {
        coursService.delete(id, authentication.getName());
    }



}