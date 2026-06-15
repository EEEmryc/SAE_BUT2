package sae.learnhub.learnhub.application.auth;

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
import sae.learnhub.learnhub.application.port.AccountNotificationSender;
import sae.learnhub.learnhub.application.port.TokenProvider;
import sae.learnhub.learnhub.domain.model.RefreshToken;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.model.UserRole;
import sae.learnhub.learnhub.domain.repository.IRefreshTokenRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUserRepository userRepository;
    private final IRefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final AccountNotificationSender notificationSender;

    public record RegisterCommand(String nom, String prenom, String email, String password, String role, String statut) {}
    public record LoginCommand(String email, String password) {}
    public record ResetPasswordCommand(String token, String newPassword) {}
    
    public record AuthResult(String token, String refreshToken) {}
    public record UserResult(
            Long id,
            String nom,
            String prenom,
            String email,
            String role,
            String statut,
            LocalDateTime dateCreation
    ) {}
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
        if (!UserRole.ETUDIANT.name().equals(role)) {
            throw new BusinessRuleException(
                    "L'inscription publique est réservée aux étudiants");
        }

        User user = new User();
        user.setNom(command.nom());
        user.setPrenom(command.prenom());
        user.setEmail(command.email());
        user.setPassword(passwordEncoder.encode(command.password()));
        user.setRole(role);
        user.setStatut(normalizeStatus(command.statut()));

        User savedUser = userRepository.save(user);
        
        return new UserResult(savedUser.getId(), savedUser.getNom(), savedUser.getPrenom(),
                savedUser.getEmail(), savedUser.getRole(), savedUser.getStatut(),
                savedUser.getDateCreation());
    }

    @Transactional
    public AuthResult login(LoginCommand command) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(command.email(), command.password()));

            refreshTokenRepository.deleteByEmail(command.email());

            String refreshTokenString = tokenProvider.generateRefreshToken(command.email());
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setToken(refreshTokenString);
            refreshToken.setEmail(command.email());
            refreshToken.setExpiryDate(Instant.now().plusMillis(tokenProvider.getRefreshExpirationTime()));
            refreshToken.setRevoked(false);
            refreshTokenRepository.save(refreshToken);

            return new AuthResult(tokenProvider.generateToken(command.email()), refreshTokenString);

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

        String email = tokenProvider.extractUsername(refreshTokenStr);
        return new RefreshResult(tokenProvider.generateToken(email));
    }

    public UserResult getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        return new UserResult(
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getRole(),
                normalizeStatus(user.getStatut()),
                user.getDateCreation());
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

    public boolean forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiration(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
        return notificationSender.sendPasswordReset(user.getEmail(), user.getPrenom(), token);
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

    private String normalizeStatus(String status) {
        String normalized = status == null || status.isBlank()
                ? "ACTIF"
                : status.trim().toUpperCase(Locale.ROOT);
        if (!"ACTIF".equals(normalized) && !"INACTIF".equals(normalized)) {
            throw new BusinessRuleException("Statut invalide. Valeurs acceptées : ACTIF, INACTIF");
        }
        return normalized;
    }
}
