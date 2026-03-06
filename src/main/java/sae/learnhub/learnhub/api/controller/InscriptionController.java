package sae.learnhub.learnhub.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sae.learnhub.learnhub.application.Service.InscriptionService;
import sae.learnhub.learnhub.domain.model.Inscription;

import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
@RequiredArgsConstructor
public class InscriptionController {

    private final InscriptionService inscriptionService;

    @PostMapping("/cours/{coursId}")
    public ResponseEntity<Inscription> sInscrire(@PathVariable Long coursId, Authentication authentication) {
        return ResponseEntity.ok(inscriptionService.inscrireEleve(coursId, authentication.getName()));
    }

    @GetMapping("/mes-inscriptions")
    public ResponseEntity<List<Inscription>> getMesInscriptions(Authentication authentication) {
        return ResponseEntity.ok(inscriptionService.getInscriptionsParEleve(authentication.getName()));
    }
}