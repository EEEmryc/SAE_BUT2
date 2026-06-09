package sae.elearning.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sae.elearning.api.dto.*;
import sae.elearning.api.mapper.AuthMapper;
import sae.elearning.application.service.AuthService;
import sae.elearning.application.service.TokenBlacklistService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;
    private final AuthMapper authMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(
                authMapper.toResponse(authService.register(authMapper.toCommand(request)))
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                authMapper.toResponse(authService.login(authMapper.toCommand(request)))
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refreshToken(@RequestHeader("X-Refresh-Token") String refreshToken) {
        return ResponseEntity.ok(
                authMapper.toResponse(authService.refreshToken(refreshToken))
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("X-Refresh-Token") String refreshToken,
            HttpServletRequest request) {
        authService.logout(refreshToken);
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            tokenBlacklistService.blacklistToken(authHeader.substring(7));
        }
        return ResponseEntity.ok(Map.of("message", "Déconnexion réussie"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String token = authService.forgotPassword(request.email());
        return ResponseEntity.ok(Map.of(
                "message", "Jeton de réinitialisation généré",
                "token", token
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(authMapper.toCommand(request));
        return ResponseEntity.ok(Map.of("message", "Mot de passe réinitialisé avec succès"));
    }
}