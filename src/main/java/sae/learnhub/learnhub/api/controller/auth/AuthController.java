package sae.learnhub.learnhub.api.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import sae.learnhub.learnhub.api.dto.auth.AuthResponse;
import sae.learnhub.learnhub.api.dto.auth.ForgotPasswordRequest;
import sae.learnhub.learnhub.api.dto.auth.LoginRequest;
import sae.learnhub.learnhub.api.dto.auth.ResetPasswordRequest;
import sae.learnhub.learnhub.api.dto.auth.RegisterRequest;
import sae.learnhub.learnhub.api.dto.auth.RefreshResponse;
import sae.learnhub.learnhub.api.dto.user.UserResponse;
import sae.learnhub.learnhub.api.mapper.AuthMapper;
import sae.learnhub.learnhub.application.auth.AuthService;
import sae.learnhub.learnhub.infrastructure.security.TokenBlacklistService;

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
                authMapper.toResponse(authService.register(authMapper.toCommand(request))));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                authMapper.toResponse(authService.login(authMapper.toCommand(request))));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refreshToken(@RequestHeader("X-Refresh-Token") String refreshToken) {
        return ResponseEntity.ok(
                authMapper.toResponse(authService.refreshToken(refreshToken)));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(
                authMapper.toResponse(authService.getCurrentUser(authentication.getName())));
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
        boolean emailSent = authService.forgotPassword(request.email());
        return ResponseEntity.ok(Map.of(
                "message", emailSent
                        ? "Un email de réinitialisation vous a été envoyé"
                        : "La demande a été enregistrée, mais l'email n'a pas pu être envoyé"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(authMapper.toCommand(request));
        return ResponseEntity.ok(Map.of("message", "Mot de passe réinitialisé avec succès"));
    }
}
