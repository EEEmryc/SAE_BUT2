package sae.learnhub.learnhub.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import sae.learnhub.learnhub.application.Service.RessourceService;
import sae.learnhub.learnhub.domain.dto.RessourceRequest;
import sae.learnhub.learnhub.domain.dto.RessourceResponse;
import java.util.List;

@RestController
@RequestMapping("/api/cours/{coursId}/chapitres/{chapitreId}/ressources")
@RequiredArgsConstructor
public class RessourceController {

    private final RessourceService ressourceService;

    @GetMapping
    public List<RessourceResponse> getAllRessourcesByChapitre(@PathVariable Long coursId, @PathVariable Long chapitreId) {
        return ressourceService.findByChapitreId(coursId, chapitreId);
    }

    @PostMapping
    public RessourceResponse createRessource(@PathVariable Long coursId,
                                         @PathVariable Long chapitreId,
                                         @RequestBody RessourceRequest request,
                                         Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise");
        }
        return ressourceService.create(coursId, chapitreId, request, authentication.getName());
    }

    @PutMapping("/{ressourceId}")
    public RessourceResponse updateRessource(@PathVariable Long coursId,
                                          @PathVariable Long chapitreId,
                                          @PathVariable Long ressourceId,
                                          @RequestBody RessourceRequest request,
                                          Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise");
        }
        return ressourceService.update(coursId, chapitreId, ressourceId, request, authentication.getName());
    }

    @DeleteMapping("/{ressourceId}")
    public void deleteRessource(@PathVariable Long coursId,
                              @PathVariable Long chapitreId,
                              @PathVariable Long ressourceId,
                              Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise");
        }
        ressourceService.delete(coursId, chapitreId, ressourceId, authentication.getName());
    }
}
