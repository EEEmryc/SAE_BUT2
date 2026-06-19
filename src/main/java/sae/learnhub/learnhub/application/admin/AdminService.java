package sae.learnhub.learnhub.application.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.application.exception.ResourceNotFoundException;
import sae.learnhub.learnhub.application.user.UserService;
import sae.learnhub.learnhub.domain.model.CoursStatut;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.model.UserStatut;
import sae.learnhub.learnhub.domain.repository.ICoursRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final IUserRepository userRepository;
    private final ICoursRepository coursRepository;

    public GlobalStatistics getGlobalStatistics() {
        long totalUsers = userRepository.count();
        long activeCourses = coursRepository.countByStatut(CoursStatut.PUBLISHED.name());
        return new GlobalStatistics(totalUsers, activeCourses);
    }

    @Transactional
    public UserService.UserResult toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        String newStatut = UserStatut.INACTIF.name().equalsIgnoreCase(user.getStatut())
                ? UserStatut.ACTIF.name()
                : UserStatut.INACTIF.name();
        user.setStatut(newStatut);
        User saved = userRepository.save(user);

        return new UserService.UserResult(
                saved.getId(),
                saved.getNom(),
                saved.getPrenom(),
                saved.getEmail(),
                saved.getRole(),
                saved.getStatut(),
                saved.getDateCreation());
    }

    @Transactional
    public UserService.UserResult updateUserEmail(Long userId, String newEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        String normalizedEmail = newEmail.trim().toLowerCase(java.util.Locale.ROOT);

        userRepository.findByEmail(normalizedEmail)
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> {
                    throw new BusinessRuleException("Cet email est déjà utilisé");
                });

        user.setEmail(normalizedEmail);
        User saved = userRepository.save(user);

        return new UserService.UserResult(
                saved.getId(),
                saved.getNom(),
                saved.getPrenom(),
                saved.getEmail(),
                saved.getRole(),
                saved.getStatut(),
                saved.getDateCreation());
    }

    public record GlobalStatistics(long totalUsers, long activeCourses) {}
}
