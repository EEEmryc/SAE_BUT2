package sae.learnhub.learnhub.api.controller.Ressources;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sae.learnhub.learnhub.api.dto.Ressources_DTO.RessourceResponse;
import sae.learnhub.learnhub.api.mapper.RessourceMapper;
import sae.learnhub.learnhub.application.Ressource_Service.RessourceService;

import java.util.List;

@RestController
@RequestMapping("/api/cours/{coursId}/ressources")
@RequiredArgsConstructor
public class CourseResourceController {

    private final RessourceService ressourceService;

    @GetMapping
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<List<RessourceResponse>> getCourseResources(
            @PathVariable Long coursId,
            Authentication authentication) {
        return ResponseEntity.ok(
                ressourceService.findByCoursId(coursId, authentication.getName()).stream()
                        .map(RessourceMapper::toResponse)
                        .toList());
    }
}
