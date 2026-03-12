package sae.learnhub.learnhub.api.controller.Messagerie;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import sae.learnhub.learnhub.api.dto.Messagerie_DTO.MessagerieRequest;
import sae.learnhub.learnhub.api.dto.Messagerie_DTO.MessagerieResponse;
import sae.learnhub.learnhub.api.dto.Messagerie_DTO.RepondreRequest;
import sae.learnhub.learnhub.application.Messagerie_Service.MessagerieService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Messagerie", description = "Envoi et réception de messages entre utilisateurs")
public class MessagerieController {

    private final MessagerieService messagerieService;

    @PostMapping
    @Operation(summary = "Envoyer un message", description = "Envoie un message à un autre utilisateur (ADMIN/PROFESSEUR/ETUDIANT)")
    public ResponseEntity<MessagerieResponse> envoyer(
            @Valid @RequestBody MessagerieRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(messagerieService.envoyer(request, authentication.getName()));
    }

    @GetMapping("/recus")
    @Operation(summary = "Boîte de réception", description = "Liste tous les messages reçus par l'utilisateur connecté")
    public ResponseEntity<List<MessagerieResponse>> getInbox(Authentication authentication) {
        return ResponseEntity.ok(messagerieService.getInbox(authentication.getName()));
    }

    @GetMapping("/envoyes")
    @Operation(summary = "Messages envoyés", description = "Liste tous les messages envoyés par l'utilisateur connecté")
    public ResponseEntity<List<MessagerieResponse>> getOutbox(Authentication authentication) {
        return ResponseEntity.ok(messagerieService.getOutbox(authentication.getName()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un message", description = "Récupère un message par son id — marque automatiquement comme lu si le destinataire l'ouvre")
    public ResponseEntity<MessagerieResponse> getById(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(messagerieService.getById(id, authentication.getName()));
    }

    @PostMapping("/{id}/repondre")
    @Operation(summary = "Répondre à un message", description = "Envoie une réponse à l'expéditeur d'un message reçu")
    public ResponseEntity<MessagerieResponse> repondre(
            @PathVariable Long id,
            @Valid @RequestBody RepondreRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(messagerieService.repondre(id, request.getContenu(), authentication.getName()));
    }

    @GetMapping("/non-lus")
    @Operation(summary = "Nombre de messages non lus", description = "Retourne le nombre de messages non lus dans la boîte de réception")
    public ResponseEntity<Map<String, Long>> countUnread(Authentication authentication) {
        long count = messagerieService.countUnread(authentication.getName());
        return ResponseEntity.ok(Map.of("nonLus", count));
    }
}
