package sae.learnhub.learnhub.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sae.learnhub.learnhub.application.Service.InscriptionService;
import sae.learnhub.learnhub.domain.model.Inscription;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inscriptions")
@RequiredArgsConstructor
public class InscriptionController {

    private final InscriptionService inscriptionService;

    /**
     * Endpoint pour qu'un élève demande son inscription à un cours.
     */
    @PostMapping("/cours/{coursId}")
    public ResponseEntity<Inscription> sInscrire(@PathVariable Long coursId, Authentication authentication) {
        return ResponseEntity.ok(inscriptionService.inscrireEleve(coursId, authentication.getName()));
    }

    /**
     * Endpoint pour qu'un élève consulte ses propres inscriptions.
     */
    @GetMapping("/mes-inscriptions")
    public ResponseEntity<List<Inscription>> getMesInscriptions(Authentication authentication) {
        return ResponseEntity.ok(inscriptionService.getInscriptionsParEleve(authentication.getName()));
    }

    /**
     * Endpoint pour valider ou refuser une inscription.
     * Accessible par l'ADMINISTRATEUR ou le PROFESSEUR responsable du cours.
     * Corps de la requête attendu : { "statut": "VALIDE" } ou { "statut": "REFUSE" }
     */
    @PatchMapping("/{id}/statut")
    public ResponseEntity<Inscription> validerInscription(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        String nouveauStatut = body.get("statut");
        return ResponseEntity.ok(inscriptionService.changerStatutInscription(id, nouveauStatut, authentication.getName()));
    }
}
