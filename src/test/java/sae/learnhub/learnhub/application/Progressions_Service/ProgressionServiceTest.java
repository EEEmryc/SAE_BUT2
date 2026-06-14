package sae.learnhub.learnhub.application.Progressions_Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sae.learnhub.learnhub.domain.model.Chapitre;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.model.Progression;
import sae.learnhub.learnhub.domain.model.Inscription;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.ICoursRepository;
import sae.learnhub.learnhub.domain.repository.IChapitreRepository;
import sae.learnhub.learnhub.domain.repository.IProgressionRepository;
import sae.learnhub.learnhub.domain.repository.IInscriptionRepository;
import sae.learnhub.learnhub.domain.repository.IRessourceRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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

    @Mock
    private IInscriptionRepository inscriptionRepository;

    @Mock
    private IRessourceRepository ressourceRepository;

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

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(eleve));
        when(chapitreRepository.findById(chapitreId)).thenReturn(Optional.of(chapitre));
        when(inscriptionRepository.existsByEleveEmailAndCoursIdAndStatut(
                email, cours.getId(), "VALIDE")).thenReturn(true);
        when(progressionRepository.findByEleveEmailAndChapitreId(email, chapitreId)).thenReturn(Optional.empty());
        when(progressionRepository.save(any(Progression.class)))
                .thenAnswer(invocation -> {
                    Progression progression = invocation.getArgument(0);
                    progression.setId(100L);
                    return progression;
                });

        ProgressionService.ProgressionResult response = progressionService.commencerChapitre(chapitreId, email);

        verify(progressionRepository).save(argThat(progression ->
                "EN_COURS".equals(progression.getStatut())
                        && progression.getPourcentage() == 0
                        && progression.getDateDebut() != null));
        assertEquals("EN_COURS", response.statut());
        assertNotNull(response.dateDebut());
    }

    @Test
    void terminerChapitre_appliqueLaTransitionMetierComplete() {
        String email = "eleve@example.com";
        Long chapitreId = 1L;

        User eleve = new User();
        eleve.setId(1L);
        eleve.setNom("Doe");
        eleve.setPrenom("Jane");

        Cours cours = new Cours();
        cours.setId(5L);
        cours.setTitre("Java");

        Chapitre chapitre = new Chapitre();
        chapitre.setId(chapitreId);
        chapitre.setTitre("Chapitre 1");
        chapitre.setCours(cours);

        Progression progression = new Progression();
        progression.setId(100L);
        progression.setEleve(eleve);
        progression.setCours(cours);
        progression.setChapitre(chapitre);
        progression.demarrer();

        when(progressionRepository.findByEleveEmailAndChapitreId(email, chapitreId))
                .thenReturn(Optional.of(progression));
        when(progressionRepository.save(progression)).thenReturn(progression);

        ProgressionService.ProgressionResult response =
                progressionService.terminerChapitre(chapitreId, email);

        verify(progressionRepository).save(argThat(saved ->
                "TERMINE".equals(saved.getStatut())
                        && saved.getPourcentage() == 100
                        && saved.getDateFin() != null));
        assertEquals("TERMINE", response.statut());
        assertEquals(100, response.pourcentage());
        assertNotNull(response.dateFin());
    }

    @Test
    void getProgressionCours_calculeLePourcentageCorrectement() {
        String email = "eleve@example.com";
        Long coursId = 5L;

        Cours cours = new Cours();
        cours.setId(coursId);
        cours.setTitre("Java");

        when(coursRepository.findById(coursId)).thenReturn(Optional.of(cours));
        when(inscriptionRepository.existsByEleveEmailAndCoursIdAndStatut(
                email, coursId, "VALIDE")).thenReturn(true);
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

    @Test
    void getToutesMesProgressions_inclutUnCoursNonCommence() {
        String email = "eleve@example.com";
        Cours cours = new Cours();
        cours.setId(5L);
        cours.setTitre("Java");
        Inscription inscription = new Inscription();
        inscription.setCours(cours);

        when(inscriptionRepository.findByEleveEmailAndStatut(email, "VALIDE"))
                .thenReturn(List.of(inscription));
        when(coursRepository.findById(5L)).thenReturn(Optional.of(cours));
        when(inscriptionRepository.existsByEleveEmailAndCoursIdAndStatut(
                email, 5L, "VALIDE")).thenReturn(true);
        when(chapitreRepository.findByCoursIdOrderByOrdreAsc(5L))
                .thenReturn(List.of(new Chapitre(), new Chapitre()));
        when(progressionRepository.findByEleveEmailAndCoursId(email, 5L))
                .thenReturn(List.of());

        List<ProgressionService.ProgressionCoursResult> result =
                progressionService.getToutesMesProgressions(email);

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).pourcentageGlobal());
        assertEquals(2, result.get(0).totalChapitres());
    }
}
