package sae.learnhub.learnhub.application.User_Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.application.exception.ResourceNotFoundException;
import sae.learnhub.learnhub.application.port.AccountNotificationSender;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.model.UserRole;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Set<String> ALLOWED_STATUSES = Set.of("ACTIF", "INACTIF");

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountNotificationSender notificationSender;

    public record UserCommand(
            String nom,
            String prenom,
            String email,
            String password,
            String role,
            String statut
    ) {
    }

    public record UserResult(
            Long id,
            String nom,
            String prenom,
            String email,
            String role,
            String statut,
            LocalDateTime dateCreation
    ) {
    }

    public record UserCreationResult(UserResult user, boolean invitationEmailSent) {
    }

    public List<UserResult> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResult).toList();
    }

    @Transactional
    public UserCreationResult createUser(UserCommand command) {
        String email = normalizeEmail(command.email());
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessRuleException("Cette adresse email est déjà utilisée");
        }
        validatePassword(command.password());

        String resetToken = UUID.randomUUID().toString();
        User user = new User();
        user.setNom(command.nom().trim());
        user.setPrenom(command.prenom().trim());
        user.setEmail(email);
        user.setRole(normalizeRole(command.role()));
        user.setStatut(normalizeStatus(command.statut()));
        user.setPassword(passwordEncoder.encode(command.password()));
        user.setResetToken(resetToken);
        user.setResetTokenExpiration(LocalDateTime.now().plusHours(1));

        User savedUser = userRepository.save(user);
        boolean invitationEmailSent = notificationSender.sendAccountInvitation(
                savedUser.getEmail(),
                savedUser.getPrenom(),
                resetToken);

        return new UserCreationResult(toResult(savedUser), invitationEmailSent);
    }

    @Transactional
    public UserResult updateUser(Long id, UserCommand command) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur non trouvé avec l'id : " + id));

        String email = normalizeEmail(command.email());
        userRepository.findByEmail(email)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessRuleException("Cette adresse email est déjà utilisée");
                });

        user.setNom(command.nom().trim());
        user.setPrenom(command.prenom().trim());
        user.setEmail(email);
        user.setRole(normalizeRole(command.role()));
        user.setStatut(normalizeStatus(command.statut()));

        if (command.password() != null && !command.password().isBlank()) {
            validatePassword(command.password());
            user.setPassword(passwordEncoder.encode(command.password()));
        }

        return toResult(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new BusinessRuleException("Le mot de passe est obligatoire");
        }
        if (password.length() < 8) {
            throw new BusinessRuleException("Le mot de passe doit contenir au moins 8 caractères");
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeRole(String role) {
        String normalizedRole = role == null ? "" : role.trim().toUpperCase(Locale.ROOT);
        if (normalizedRole.startsWith("ROLE_")) {
            normalizedRole = normalizedRole.substring(5);
        }

        try {
            return UserRole.valueOf(normalizedRole).name();
        } catch (IllegalArgumentException exception) {
            throw new BusinessRuleException(
                    "Rôle invalide. Valeurs acceptées : ADMIN, PROFESSEUR, ETUDIANT");
        }
    }

    private String normalizeStatus(String status) {
        String normalizedStatus = status == null || status.isBlank()
                ? "ACTIF"
                : status.trim().toUpperCase(Locale.ROOT);

        if (!ALLOWED_STATUSES.contains(normalizedStatus)) {
            throw new BusinessRuleException("Statut invalide. Valeurs acceptées : ACTIF, INACTIF");
        }
        return normalizedStatus;
    }

    private UserResult toResult(User user) {
        return new UserResult(
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getRole(),
                normalizeStatus(user.getStatut()),
                user.getDateCreation());
    }
}
