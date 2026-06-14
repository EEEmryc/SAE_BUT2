package sae.learnhub.learnhub.application.Cours_Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sae.learnhub.learnhub.application.exception.AccessDeniedException;
import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.application.exception.ResourceNotFoundException;
import sae.learnhub.learnhub.application.port.ResourceFileStorage;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.ICoursRepository;
import sae.learnhub.learnhub.domain.repository.IChapitreRepository;
import sae.learnhub.learnhub.domain.repository.IInscriptionRepository;
import sae.learnhub.learnhub.domain.repository.IProgressionRepository;
import sae.learnhub.learnhub.domain.repository.IRessourceRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoursServiceTest {

    @Mock
    private ICoursRepository coursRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IInscriptionRepository inscriptionRepository;

    @Mock
    private IChapitreRepository chapitreRepository;

    @Mock
    private IRessourceRepository ressourceRepository;

    @Mock
    private IProgressionRepository progressionRepository;

    @Mock
    private ResourceFileStorage fileStorage;

    @InjectMocks
    private CoursService coursService;

    @Test
    void create_quandProfExiste_sauvegardeEtRetourneLeBonTitre() {
        String email = "prof@example.com";

        User prof = new User();
        prof.setId(1L);
        prof.setNom("Doe");
        prof.setPrenom("John");
        prof.setEmail(email);

        CoursService.CoursCommand command = new CoursService.CoursCommand(
                "Introduction à Java", "Cours pour débutants", null, null);

        Cours savedCours = new Cours();
        savedCours.setId(10L);
        savedCours.setTitre(command.titre());
        savedCours.setDescription(command.description());
        savedCours.setProf(prof);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(prof));
        when(coursRepository.save(any(Cours.class))).thenReturn(savedCours);

        CoursService.CoursResult response = coursService.create(command, email);

        verify(coursRepository).save(any(Cours.class));
        assertEquals("Introduction à Java", response.titre());
    }

    @Test
    void delete_quandCoursIntrouvable_lanceErreurMetier() {
        when(coursRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> coursService.delete(99L, "prof@example.com"));

        assertEquals("Cours introuvable", exception.getMessage());
        verify(coursRepository, never()).deleteById(any());
    }

    @Test
    void delete_quandProfNeCorrespondPas_lanceErreurAcces() {
        User prof = new User();
        prof.setEmail("autre.prof@example.com");

        Cours cours = new Cours();
        cours.setId(1L);
        cours.setProf(prof);

        when(coursRepository.findById(1L)).thenReturn(Optional.of(cours));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> coursService.delete(1L, "prof@example.com"));

        assertEquals("Vous n'êtes pas responsable de ce cours", exception.getMessage());
        verify(coursRepository, never()).deleteById(any());
    }

    @Test
    void create_normaliseLeStatutDuCours() {
        String email = "prof@example.com";
        User prof = new User();
        prof.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(prof));
        when(coursRepository.save(any(Cours.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CoursService.CoursResult result = coursService.create(
                new CoursService.CoursCommand(" Java ", " Description du cours ", "published", true),
                email);

        assertEquals("PUBLISHED", result.statut());
        assertEquals("Java", result.titre());
    }

    @Test
    void create_refuseUnStatutInconnu() {
        User prof = new User();
        prof.setEmail("prof@example.com");
        when(userRepository.findByEmail("prof@example.com")).thenReturn(Optional.of(prof));

        assertThrows(BusinessRuleException.class, () -> coursService.create(
                new CoursService.CoursCommand("Java", "Description du cours", "PUBLIC", true),
                "prof@example.com"));
        verify(coursRepository, never()).save(any());
    }
}
