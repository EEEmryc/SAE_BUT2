package sae.learnhub.learnhub.application.Inscriptions_Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.model.Inscription;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.ICoursRepository;
import sae.learnhub.learnhub.domain.repository.IInscriptionRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InscriptionServiceTest {

    @Mock
    private IInscriptionRepository inscriptionRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private ICoursRepository coursRepository;

    @InjectMocks
    private InscriptionService inscriptionService;

    @Test
    void inscrireEleve_quandDejaInscrit_lanceErreurMetier() {
        String email = "eleve@example.com";
        Long coursId = 5L;

        User eleve = new User();
        eleve.setId(1L);
        eleve.setEmail(email);

        Cours cours = new Cours();
        cours.setId(coursId);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(eleve));
        when(coursRepository.findById(coursId)).thenReturn(Optional.of(cours));
        when(inscriptionRepository.existsByEleveIdAndCoursId(eleve.getId(), cours.getId())).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> inscriptionService.inscrireEleve(coursId, email));

        assertEquals("Déjà inscrit à ce cours", exception.getMessage());
        verify(inscriptionRepository, never()).save(any());
    }

    @Test
    void inscrireEleve_quandToutEstOk_sauvegardeLInscription() {
        String email = "eleve@example.com";
        Long coursId = 5L;

        User eleve = new User();
        eleve.setId(1L);
        eleve.setEmail(email);

        Cours cours = new Cours();
        cours.setId(coursId);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(eleve));
        when(coursRepository.findById(coursId)).thenReturn(Optional.of(cours));
        when(inscriptionRepository.existsByEleveIdAndCoursId(eleve.getId(), cours.getId())).thenReturn(false);
        when(inscriptionRepository.save(any(Inscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        inscriptionService.inscrireEleve(coursId, email);

        verify(inscriptionRepository).save(any(Inscription.class));
    }
}
