package sae.learnhub.learnhub.api.controller.signalement;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sae.learnhub.learnhub.api.dto.signalement.SignalementRequest;
import sae.learnhub.learnhub.api.dto.signalement.SignalementResponse;
import sae.learnhub.learnhub.api.dto.signalement.SignalementStatutRequest;
import sae.learnhub.learnhub.api.mapper.SignalementMapper;
import sae.learnhub.learnhub.application.signalement.SignalementService;

import java.util.List;

@RestController
@RequestMapping("/api/signalements")
@RequiredArgsConstructor
@Tag(name = "Signalements", description = "Création et traitement des signalements")
public class SignalementController {

    private final SignalementService signalementService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ETUDIANT', 'PROFESSEUR')")
    @Operation(summary = "Créer un signalement")
    public ResponseEntity<SignalementResponse> create(
            @Valid @RequestBody SignalementRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                SignalementMapper.toResponse(
                        signalementService.create(
                                SignalementMapper.toCommand(request),
                                authentication.getName())));
    }

    @GetMapping("/mes-signalements")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'PROFESSEUR')")
    @Operation(summary = "Lister mes signalements")
    public ResponseEntity<List<SignalementResponse>> getMine(Authentication authentication) {
        return ResponseEntity.ok(
                signalementService.getMine(authentication.getName()).stream()
                        .map(SignalementMapper::toResponse)
                        .toList());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister tous les signalements")
    public ResponseEntity<List<SignalementResponse>> getAll() {
        return ResponseEntity.ok(
                signalementService.getAll().stream()
                        .map(SignalementMapper::toResponse)
                        .toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Consulter un signalement")
    public ResponseEntity<SignalementResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                SignalementMapper.toResponse(signalementService.getById(id)));
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier le statut d'un signalement")
    public ResponseEntity<SignalementResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody SignalementStatutRequest request) {
        return ResponseEntity.ok(
                SignalementMapper.toResponse(
                        signalementService.updateStatus(id, request.statut())));
    }
}