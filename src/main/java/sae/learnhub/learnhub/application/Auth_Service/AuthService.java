package sae.learnhub.learnhub.application.Auth_Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sae.learnhub.learnhub.application.exception.AuthenticationFailedException;
import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.application.exception.ResourceNotFoundException;
import sae.learnhub.learnhub.domain.model.RefreshToken;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.model.UserRole;
import sae.learnhub.learnhub.domain.repository.IRefreshTokenRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;
import sae.learnhub.learnhub.infrastructure.config.JwtUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUserRepository userRepository;
    private final IRefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public record RegisterCommand(String nom, String prenom, String email, String password, String role, String statut) {}
    public record LoginCommand(String email, String password) {}
    public record ResetPasswordCommand(String token, String newPassword) {}
    
    public record AuthResult(String token, String refreshToken) {}
    public record UserResult(Long id, String nom, String prenom, String email, String role, String statut) {}
    public record RefreshResult(String token) {}

    @Transactional
    public UserResult register(RegisterCommand command) {
        if (userRepository.findByEmail(command.email()).isPresent()) {
            throw new BusinessRuleException("Email déjà existant");
        }

        String role = command.role();
        if (role != null && role.startsWith("ROLE_")) {
            role = role.substring(5);
        }
        if (role != null) {
            role = role.toUpperCase();
        }
        if (role == null || (!role.equals(UserRole.ADMIN.name()) && !role.equals(UserRole.PROFESSEUR.name()) && !role.equals(UserRole.ETUDIANT.name()))) {
            throw new BusinessRuleException("Rôle invalide. Valeurs acceptées : ADMIN, PROFESSEUR, ETUDIANT");
        }

        User user = new User();
        user.setNom(command.nom());
        user.setPrenom(command.prenom());
        user.setEmail(command.email());
        user.setPassword(passwordEncoder.encode(command.password()));
        user.setRole(role);
        user.setStatut(command.statut() != null && !command.statut().isBlank() ? command.statut() : "ACTIF");

        User savedUser = userRepository.save(user);
        
        return new UserResult(savedUser.getId(), savedUser.getNom(), savedUser.getPrenom(),
                savedUser.getEmail(), savedUser.getRole(), savedUser.getStatut());
    }

    @Transactional
    public AuthResult login(LoginCommand command) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(command.email(), command.password()));

            refreshTokenRepository.deleteByEmail(command.email());

            String refreshTokenString = jwtUtils.generateRefreshToken(command.email());
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setToken(refreshTokenString);
            refreshToken.setEmail(command.email());
            refreshToken.setExpiryDate(Instant.now().plusMillis(jwtUtils.getRefreshExpirationTime()));
            refreshToken.setRevoked(false);
            refreshTokenRepository.save(refreshToken);

            return new AuthResult(jwtUtils.generateToken(command.email()), refreshTokenString);

        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException("Email ou mot de passe invalide");
        }
    }

    public RefreshResult refreshToken(String refreshTokenStr) {
        if (refreshTokenStr == null || refreshTokenStr.isEmpty()) {
            throw new BusinessRuleException("Refresh token est requis");
        }

        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(refreshTokenStr);
        if (tokenOpt.isEmpty() || tokenOpt.get().isRevoked()) {
            throw new AuthenticationFailedException("Refresh token invalide");
        }

        RefreshToken token = tokenOpt.get();
        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new AuthenticationFailedException("Refresh token expiré");
        }

        String email = jwtUtils.extractUsername(refreshTokenStr);
        return new RefreshResult(jwtUtils.generateToken(email));
    }

    public void logout(String refreshTokenStr) {
        if (refreshTokenStr != null && !refreshTokenStr.isEmpty()) {
            Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(refreshTokenStr);
            tokenOpt.ifPresent(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            });
        }
    }

    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiration(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
        return token;
    }

    public void resetPassword(ResetPasswordCommand command) {
        User user = userRepository.findByResetToken(command.token())
                .orElseThrow(() -> new BusinessRuleException("Token invalide"));

        if (user.getResetTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Token expiré");
        }

        user.setPassword(passwordEncoder.encode(command.newPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiration(null);
        userRepository.save(user);
    }
}
