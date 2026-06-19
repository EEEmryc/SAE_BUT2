package sae.learnhub.learnhub.application.admin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.application.exception.ResourceNotFoundException;
import sae.learnhub.learnhub.application.user.UserService;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.model.UserStatut;
import sae.learnhub.learnhub.domain.repository.ICoursRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private ICoursRepository coursRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void toggleUserStatus_passeDeActifAInactif() {
        User user = buildUser(1L, "ETUDIANT", UserStatut.ACTIF.name());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserService.UserResult result = adminService.toggleUserStatus(1L);

        assertEquals(UserStatut.INACTIF.name(), result.statut());
        verify(userRepository).save(user);
    }

    @Test
    void toggleUserStatus_passeDeInactifAActif() {
        User user = buildUser(2L, "PROFESSEUR", UserStatut.INACTIF.name());

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserService.UserResult result = adminService.toggleUserStatus(2L);

        assertEquals(UserStatut.ACTIF.name(), result.statut());
        verify(userRepository).save(user);
    }

    @Test
    void toggleUserStatus_lanceNotFoundException_siUserIntrouvable() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> adminService.toggleUserStatus(99L));

        assertEquals("Utilisateur introuvable", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserEmail_changeBienLEmail() {
        User user = buildUser(1L, "ETUDIANT", UserStatut.ACTIF.name());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("nouveau@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserService.UserResult result = adminService.updateUserEmail(1L, "Nouveau@Test.com");

        assertEquals("nouveau@test.com", result.email());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserEmail_lanceNotFoundException_siUserIntrouvable() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> adminService.updateUserEmail(99L, "x@test.com"));

        assertEquals("Utilisateur introuvable", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserEmail_lanceBusinessRuleException_siEmailDejaUtilise() {
        User user = buildUser(1L, "ETUDIANT", UserStatut.ACTIF.name());
        User autreUser = buildUser(2L, "PROFESSEUR", UserStatut.ACTIF.name());
        autreUser.setEmail("pris@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("pris@test.com")).thenReturn(Optional.of(autreUser));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> adminService.updateUserEmail(1L, "pris@test.com"));

        assertEquals("Cet email est déjà utilisé", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    private User buildUser(Long id, String role, String statut) {
        User user = new User();
        user.setId(id);
        user.setNom("Dupont");
        user.setPrenom("Marie");
        user.setEmail("marie@test.com");
        user.setRole(role);
        user.setStatut(statut);
        return user;
    }
}
