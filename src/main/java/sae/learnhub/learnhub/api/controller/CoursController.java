package sae.learnhub.learnhub.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sae.learnhub.learnhub.application.Service.CoursService;
import sae.learnhub.learnhub.domain.model.Cours;

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
    public Cours creatCours(@RequestBody Cours cours) {
        return coursService.create(cours);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PROF')")
    public Cours updateCours(@PathVariable Long id,
                        @RequestBody Cours cours) {
        return coursService.update(id, cours);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PROF')")
    public void supprimerCours(@PathVariable Long id) {
        coursService.delete(id);
    }



}