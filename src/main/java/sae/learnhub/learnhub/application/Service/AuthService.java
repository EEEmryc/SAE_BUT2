package sae.learnhub.learnhub.application.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sae.learnhub.learnhub.config.JwtUtils;
import sae.learnhub.learnhub.domain.dto.AuthResponse;
import sae.learnhub.learnhub.domain.dto.LoginRequest;
import sae.learnhub.learnhub.domain.dto.RefreshResponse;
import sae.learnhub.learnhub.domain.dto.RegisterRequest;
import sae.learnhub.learnhub.domain.dto.UserResponse;
import sae.learnhub.learnhub.domain.model.RefreshToken;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.RefreshTokenRepository;
import sae.learnhub.learnhub.domain.repository.UserRepository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email deja exist");
        }
        
        User user = new User();
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_" + request.getRole());
        user.setStatut(request.getStatut());
        
        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser.getId(), savedUser.getNom(), savedUser.getPrenom(), 
                              savedUser.getEmail(), savedUser.getRole(), savedUser.getStatut());
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            // Supprimer ancien refresh token s'il existe
            refreshTokenRepository.deleteByEmail(request.getEmail());

            // Créer nouveau refresh token
            String refreshTokenString = jwtUtils.generateRefreshToken(request.getEmail());
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setToken(refreshTokenString);
            refreshToken.setEmail(request.getEmail());
            refreshToken.setExpiryDate(Instant.now().plusMillis(jwtUtils.getRefreshExpirationTime()));
            refreshToken.setRevoked(false);
            refreshTokenRepository.save(refreshToken);

            return new AuthResponse(jwtUtils.generateToken(request.getEmail()), refreshTokenString);

        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Email ou mot de passe");
        }
    }

    public RefreshResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token est requis");
        }

        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(refreshToken);
        if (tokenOpt.isEmpty() || tokenOpt.get().isRevoked()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        RefreshToken token = tokenOpt.get();
        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        if (!jwtUtils.isRefreshToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token type");
        }

        String email = jwtUtils.extractUsername(refreshToken);
        return new RefreshResponse(jwtUtils.generateToken(email));
    }

    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token est requis");
        }

        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(refreshToken);
        if (tokenOpt.isPresent()) {
            RefreshToken token = tokenOpt.get();
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        }
    }
}
