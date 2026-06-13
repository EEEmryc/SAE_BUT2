package sae.learnhub.learnhub.api.controller.Auth_Debug_Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import sae.learnhub.learnhub.api.dto.Auth_DTO.AuthResponse;
import sae.learnhub.learnhub.api.dto.Auth_DTO.ForgotPasswordRequest;
import sae.learnhub.learnhub.api.dto.Auth_DTO.LoginRequest;
import sae.learnhub.learnhub.api.dto.Auth_DTO.ResetPasswordRequest;
import sae.learnhub.learnhub.api.dto.Register.RegisterRequest;
import sae.learnhub.learnhub.api.dto.Stat_Refresh_DTO.RefreshResponse;
import sae.learnhub.learnhub.api.dto.User_DTO.UserResponse;
import sae.learnhub.learnhub.api.mapper.AuthMapper;
import sae.learnhub.learnhub.application.Auth_Service.AuthService;
import sae.learnhub.learnhub.application.Custom_Token_Service.TokenBlacklistService;

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

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(
                authMapper.toResponse(authService.getCurrentUser(authentication.getName()))
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
