package sae.learnhub.learnhub.application.Ressource_Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sae.learnhub.learnhub.application.exception.AccessDeniedException;
import sae.learnhub.learnhub.application.exception.ResourceNotFoundException;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.IChapitreRepository;
import sae.learnhub.learnhub.domain.repository.ICoursRepository;
import sae.learnhub.learnhub.domain.repository.IInscriptionRepository;
import sae.learnhub.learnhub.domain.repository.IRessourceRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceFileAccessServiceTest {

    @Mock
    private ICoursRepository coursRepository;
    @Mock
    private IChapitreRepository chapitreRepository;
    @Mock
    private IRessourceRepository ressourceRepository;
    @Mock
    private IInscriptionRepository inscriptionRepository;

    @InjectMocks
    private ResourceFileAccessService service;

    @Test
    void refuseUnEtudiantNonInscritMemeAvecLaCleDuFichier() {
        Cours cours = courseWithFile();
        when(coursRepository.findByFichierPrincipalUrl(
                "/api/files/resources/course.pdf")).thenReturn(Optional.of(cours));
        when(inscriptionRepository.existsByEleveEmailAndCoursIdAndStatut(
                "student@test.com", 5L, "VALIDE")).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.verifyAccess(
                "course.pdf",
                "student@test.com",
                false,
                true,
                false));
    }

    @Test
    void autoriseUnEtudiantAvecInscriptionValidee() {
        Cours cours = courseWithFile();
        when(coursRepository.findByFichierPrincipalUrl(
                "/api/files/resources/course.pdf")).thenReturn(Optional.of(cours));
        when(inscriptionRepository.existsByEleveEmailAndCoursIdAndStatut(
                "student@test.com", 5L, "VALIDE")).thenReturn(true);

        assertDoesNotThrow(() -> service.verifyAccess(
                "course.pdf",
                "student@test.com",
                false,
                true,
                false));
    }

    @Test
    void refuseUneCleQuiNestAssocieeAucunContenu() {
        String url = "/api/files/resources/inconnu.pdf";
        when(coursRepository.findByFichierPrincipalUrl(url)).thenReturn(Optional.empty());
        when(chapitreRepository.findByFichierPrincipalUrl(url)).thenReturn(Optional.empty());
        when(ressourceRepository.findByUrl(url)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.verifyAccess(
                "inconnu.pdf",
                "student@test.com",
                false,
                true,
                false));
    }

    private Cours courseWithFile() {
        User professor = new User();
        professor.setEmail("prof@test.com");
        Cours cours = new Cours();
        cours.setId(5L);
        cours.setProf(professor);
        cours.setFichierPrincipalUrl("/api/files/resources/course.pdf");
        return cours;
    }
}
