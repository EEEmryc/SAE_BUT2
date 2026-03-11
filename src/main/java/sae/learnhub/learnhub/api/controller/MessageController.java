package sae.learnhub.learnhub.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sae.learnhub.learnhub.application.Service.MessageService;
import sae.learnhub.learnhub.domain.dto.MessageRequest;
import sae.learnhub.learnhub.domain.dto.MessageResponse;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Messagerie", description = "Endpoints pour la gestion des messages entre étudiants et professeurs")
@SecurityRequirement(name = "bearerAuth") 
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository; 

    @PostMapping
    @Operation(summary = "Envoyer un message", description = "Permet d'envoyer un message à un autre utilisateur (étudiant ou créateur du cours)")
    public ResponseEntity<MessageResponse> envoyerMessage(
            @RequestBody MessageRequest request, 
            Authentication authentication) {
        
        String email = authentication.getName();
        
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        MessageResponse response = messageService.envoyerMessage(currentUser.getId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/conversation/{autreUserId}")
    @Operation(summary = "Récupérer une conversation", description = "Affiche l'historique des messages échangés avec un utilisateur spécifique, triés par date")
    public ResponseEntity<List<MessageResponse>> getConversation(
            @PathVariable Long autreUserId, 
            Authentication authentication) {
        
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        List<MessageResponse> conversation = messageService.getConversation(currentUser.getId(), autreUserId);
        return ResponseEntity.ok(conversation);
    }
}