package sae.learnhub.learnhub.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
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
    public List<ChapitreResponse> getAllChapitresByCours(@PathVariable Long coursId) {
        return chapitreService.findByCoursId(coursId);
    }

    @PostMapping
    public ChapitreResponse createChapitre(@PathVariable Long coursId, 
                                        @RequestBody ChapitreRequest request, 
                                        Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise");
        }
        return chapitreService.create(coursId, request, authentication.getName());
    }

    @PutMapping("/{chapitreId}")
    public ChapitreResponse updateChapitre(@PathVariable Long coursId,
                                          @PathVariable Long chapitreId,
                                          @RequestBody ChapitreRequest request,
                                          Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise");
        }
        return chapitreService.update(coursId, chapitreId, request, authentication.getName());
    }

    @DeleteMapping("/{chapitreId}")
    public void deleteChapitre(@PathVariable Long coursId,
                              @PathVariable Long chapitreId,
                              Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise");
        }
        chapitreService.delete(coursId, chapitreId, authentication.getName());
    }
}
