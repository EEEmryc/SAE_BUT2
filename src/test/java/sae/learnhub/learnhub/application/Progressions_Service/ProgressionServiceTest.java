package sae.learnhub.learnhub.application.Progressions_Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sae.learnhub.learnhub.domain.model.Chapitre;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.model.Progression;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.ICoursRepository;
import sae.learnhub.learnhub.domain.repository.IChapitreRepository;
import sae.learnhub.learnhub.domain.repository.IProgressionRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgressionServiceTest {

    @Mock
    private IProgressionRepository progressionRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private ICoursRepository coursRepository;

    @Mock
    private IChapitreRepository chapitreRepository;

    @InjectMocks
    private ProgressionService progressionService;

    @Test
    void commencerChapitre_quandChapitreExiste_sauvegardeLaProgression() {
        String email = "eleve@example.com";
        Long chapitreId = 1L;

        User eleve = new User();
        eleve.setId(1L);
        eleve.setEmail(email);
        eleve.setNom("Doe");
        eleve.setPrenom("Jane");

        Cours cours = new Cours();
        cours.setId(5L);
        cours.setTitre("Java");

        Chapitre chapitre = new Chapitre();
        chapitre.setId(chapitreId);
        chapitre.setTitre("Chapitre 1");
        chapitre.setCours(cours);

        Progression savedProgression = new Progression();
        savedProgression.setId(100L);
        savedProgression.setEleve(eleve);
        savedProgression.setCours(cours);
        savedProgression.setChapitre(chapitre);
        savedProgression.setStatut("EN_COURS");
        savedProgression.setPourcentage(0);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(eleve));
        when(chapitreRepository.findById(chapitreId)).thenReturn(Optional.of(chapitre));
        when(progressionRepository.findByEleveEmailAndChapitreId(email, chapitreId)).thenReturn(Optional.empty());
        when(progressionRepository.save(any(Progression.class))).thenReturn(savedProgression);

        ProgressionService.ProgressionResult response = progressionService.commencerChapitre(chapitreId, email);

        verify(progressionRepository).save(any(Progression.class));
        assertEquals("EN_COURS", response.statut());
    }

    @Test
    void getProgressionCours_calculeLePourcentageCorrectement() {
        String email = "eleve@example.com";
        Long coursId = 5L;

        Cours cours = new Cours();
        cours.setId(coursId);
        cours.setTitre("Java");

        when(coursRepository.findById(coursId)).thenReturn(Optional.of(cours));
        when(chapitreRepository.findByCoursIdOrderByOrdreAsc(coursId))
                .thenReturn(List.of(new Chapitre(), new Chapitre(), new Chapitre(), new Chapitre()));
        when(progressionRepository.countByEleveEmailAndCoursIdAndStatut(email, coursId, "TERMINE"))
                .thenReturn(2L);
        when(progressionRepository.findByEleveEmailAndCoursId(email, coursId))
                .thenReturn(List.of());

        ProgressionService.ProgressionCoursResult response = progressionService.getProgressionCours(coursId, email);

        assertEquals(4, response.totalChapitres());
        assertEquals(2, response.chapitresTermines());
        assertEquals(50, response.pourcentageGlobal());
    }
}
