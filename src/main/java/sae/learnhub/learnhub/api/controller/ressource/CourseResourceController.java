package sae.learnhub.learnhub.api.controller.ressource;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sae.learnhub.learnhub.api.dto.ressource.RessourceResponse;
import sae.learnhub.learnhub.api.mapper.RessourceMapper;
import sae.learnhub.learnhub.application.ressource.RessourceService;

import java.util.List;

@RestController
@RequestMapping("/api/cours/{coursId}/ressources")
@RequiredArgsConstructor
public class CourseResourceController {

    private final RessourceService ressourceService;

    @GetMapping
    @PreAuthorize("hasAnyRole('PROFESSEUR', 'ETUDIANT')")
    public ResponseEntity<List<RessourceResponse>> getCourseResources(
            @PathVariable Long coursId,
            Authentication authentication) {
        boolean isProfessor = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_PROFESSEUR"));
        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ETUDIANT"));
        return ResponseEntity.ok(
                ressourceService.findAccessibleByCoursId(
                                coursId,
                                authentication.getName(),
                                isProfessor,
                                isStudent)
                        .stream()
                        .map(RessourceMapper::toResponse)
                        .toList());
    }
}
