package sae.learnhub.learnhub.api.controller.progression;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sae.learnhub.learnhub.api.dto.progression.ProfessorStudentProgressResponse;
import sae.learnhub.learnhub.api.mapper.ProgressionMapper;
import sae.learnhub.learnhub.application.progression.ProgressionService;

import java.util.List;

@RestController
@RequestMapping("/api/progressions/professeur")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROFESSEUR')")
@Tag(
        name = "Progressions professeur",
        description = "Suivi de la progression des étudiants inscrits aux cours du professeur")
public class ProfessorProgressionController {

    private final ProgressionService progressionService;

    @GetMapping("/etudiants")
    @Operation(summary = "Lister la progression des étudiants de mes cours")
    public ResponseEntity<List<ProfessorStudentProgressResponse>> getStudentsProgress(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                progressionService
                        .getProfessorStudentProgressions(authentication.getName())
                        .stream()
                        .map(ProgressionMapper::toProfessorResponse)
                        .toList());
    }
}
