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
     * Demande d'inscription (Statut par défaut : EN_ATTENTE)
     */
    @PostMapping("/cours/{coursId}")
    public ResponseEntity<Inscription> sInscrire(@PathVariable Long coursId, Authentication authentication) {
        return ResponseEntity.ok(inscriptionService.inscrireEleve(coursId, authentication.getName()));
    }

    /**
     * Liste de TOUTES les inscriptions de l'élève (Historique)
     */
    @GetMapping("/mes-inscriptions")
    public ResponseEntity<List<Inscription>> getMesInscriptions(Authentication authentication) {
        return ResponseEntity.ok(inscriptionService.getInscriptionsParEleve(authentication.getName()));
    }

    /**
     * Liste des cours validés uniquement (Accès au contenu)
     */
    @GetMapping("/mes-cours-valides")
    public ResponseEntity<List<Inscription>> getMesCoursValides(Authentication authentication) {
        return ResponseEntity.ok(inscriptionService.getCoursValidesParEleve(authentication.getName()));
    }

    /**
     * Validation/Refus d'inscription (Réservé PROF/ADMIN)
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
