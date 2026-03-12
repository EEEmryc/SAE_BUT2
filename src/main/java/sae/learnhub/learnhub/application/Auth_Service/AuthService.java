package sae.learnhub.learnhub.application.Auth_Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import sae.learnhub.learnhub.domain.model.RefreshToken;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.RefreshTokenRepository;
import sae.learnhub.learnhub.domain.repository.UserRepository;
import sae.learnhub.learnhub.api.dto.Auth_DTO.AuthResponse;
import sae.learnhub.learnhub.api.dto.Auth_DTO.ForgotPasswordRequest;
import sae.learnhub.learnhub.api.dto.Auth_DTO.LoginRequest;
import sae.learnhub.learnhub.api.dto.Auth_DTO.ResetPasswordRequest;
import sae.learnhub.learnhub.api.dto.Register.RegisterRequest;
import sae.learnhub.learnhub.api.dto.Stat_Refresh_DTO.RefreshResponse;
import sae.learnhub.learnhub.api.dto.User_DTO.UserResponse;
import sae.learnhub.learnhub.infrastructure.config.JwtUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email dÃ©jÃ  existant");
        }

        String role = request.getRole();
        if (role != null && role.startsWith("ROLE_")) {
            role = role.substring(5);
        }
        if (role != null) {
            role = role.toUpperCase();
        }
        if (role == null || (!role.equals("ADMIN") && !role.equals("PROFESSEUR") && !role.equals("ETUDIANT"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "R\u00f4le invalide. Valeurs accept\u00e9es : ADMIN, PROFESSEUR, ETUDIANT");
        }

        User user = new User();
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setStatut(request.getStatut() != null && !request.getStatut().isBlank() ? request.getStatut() : "ACTIF");

        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser.getId(), savedUser.getNom(), savedUser.getPrenom(),
                savedUser.getEmail(), savedUser.getRole(), savedUser.getStatut());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            refreshTokenRepository.deleteByEmail(request.getEmail());

            String refreshTokenString = jwtUtils.generateRefreshToken(request.getEmail());
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setToken(refreshTokenString);
            refreshToken.setEmail(request.getEmail());
            refreshToken.setExpiryDate(Instant.now().plusMillis(jwtUtils.getRefreshExpirationTime()));
            refreshToken.setRevoked(false);
            refreshTokenRepository.save(refreshToken);

            return new AuthResponse(jwtUtils.generateToken(request.getEmail()), refreshTokenString);

        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe invalide");
        }
    }

    public RefreshResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token est requis");
        }

        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(refreshToken);
        if (tokenOpt.isEmpty() || tokenOpt.get().isRevoked()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalide");
        }

        RefreshToken token = tokenOpt.get();
        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expirÃ©");
        }

        String email = jwtUtils.extractUsername(refreshToken);
        return new RefreshResponse(jwtUtils.generateToken(email));
    }

    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(refreshToken);
            tokenOpt.ifPresent(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            });
        }
    }

    public String forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiration(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
        return token;
    }

    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token invalide"));

        if (user.getResetTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expirÃ©");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiration(null);
        userRepository.save(user);
    }
}
