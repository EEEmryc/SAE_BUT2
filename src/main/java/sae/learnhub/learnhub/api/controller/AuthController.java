package sae.learnhub.learnhub.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sae.learnhub.learnhub.application.Service.AuthService;
import sae.learnhub.learnhub.application.Service.TokenBlacklistService;
import sae.learnhub.learnhub.domain.dto.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(null);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refreshToken(@RequestHeader("X-Refresh-Token") String refreshToken) {
        try {
            return ResponseEntity.ok(authService.refreshToken(refreshToken));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(null);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("X-Refresh-Token") String refreshToken,
            HttpServletRequest request) {
        try {
            // 1. Invalidation du Refresh Token via la logique existante
            authService.logout(refreshToken);

            // 2. Blacklist de l'Access Token (JWT) actuel
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);
                tokenBlacklistService.blacklistToken(jwt);
            }

            return ResponseEntity.ok("Déconnexion réussie");
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }
}
