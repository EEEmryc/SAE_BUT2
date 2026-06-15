package sae.learnhub.learnhub.application.progression;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sae.learnhub.learnhub.application.exception.AccessDeniedException;
import sae.learnhub.learnhub.application.exception.ResourceNotFoundException;
import sae.learnhub.learnhub.domain.model.Chapitre;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.model.Progression;
import sae.learnhub.learnhub.domain.model.ProgressionStatut;
import sae.learnhub.learnhub.domain.model.Ressource;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.IChapitreRepository;
import sae.learnhub.learnhub.domain.repository.ICoursRepository;
import sae.learnhub.learnhub.domain.repository.IInscriptionRepository;
import sae.learnhub.learnhub.domain.repository.IProgressionRepository;
import sae.learnhub.learnhub.domain.repository.IRessourceRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressionService {

    private final IProgressionRepository progressionRepository;
    private final IUserRepository userRepository;
    private final ICoursRepository coursRepository;
    private final IChapitreRepository chapitreRepository;
    private final IInscriptionRepository inscriptionRepository;
    private final IRessourceRepository ressourceRepository;

    public record ProgressionResult(
            Long id,
            String statut,
            Integer pourcentage,
            LocalDateTime dateDebut,
            LocalDateTime dateMiseAJour,
            LocalDateTime dateFin,
            Long eleveId,
            String eleveNom,
            String elevePrenom,
            Long coursId,
            String coursTitre,
            Long chapitreId,
            String chapitreTitre,
            Long ressourceId,
            String ressourceNom) {}

    public record ProgressionCoursResult(
            Long coursId,
            String coursTitre,
            String profNom,
            String profPrenom,
            Integer totalChapitres,
            Integer chapitresTermines,
            Long totalRessources,
            Integer pourcentageGlobal,
            List<ProgressionResult> details) {}

    @Transactional
    public ProgressionResult commencerChapitre(Long chapitreId, String eleveEmail) {
        Chapitre chapitre = findAccessibleChapter(chapitreId, eleveEmail);
        User eleve = findStudent(eleveEmail);
        Progression progression = progressionRepository
                .findByEleveEmailAndChapitreId(eleveEmail, chapitreId)
                .orElseGet(() -> newProgression(eleve, chapitre));

        return toResult(progressionRepository.save(progression));
    }

    @Transactional
    public ProgressionResult terminerChapitre(Long chapitreId, String eleveEmail) {
        Progression progression = progressionRepository
                .findByEleveEmailAndChapitreId(eleveEmail, chapitreId)
                .orElseGet(() -> newProgression(
                        findStudent(eleveEmail),
                        findAccessibleChapter(chapitreId, eleveEmail)));
        progression.terminer();
        return toResult(progressionRepository.save(progression));
    }

    public ProgressionCoursResult getProgressionCours(Long coursId, String eleveEmail) {
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));
        validateEnrollment(coursId, eleveEmail);

        int totalChapitres = chapitreRepository
                .findByCoursIdOrderByOrdreAsc(coursId)
                .size();
        int chapitresTermines = (int) progressionRepository
                .countByEleveEmailAndCoursIdAndStatut(
                        eleveEmail,
                        coursId,
                        ProgressionStatut.TERMINE.name());
        int pourcentageGlobal = totalChapitres == 0
                ? 0
                : (chapitresTermines * 100) / totalChapitres;
        List<ProgressionResult> details = progressionRepository
                .findByEleveEmailAndCoursId(eleveEmail, coursId)
                .stream()
                .map(this::toResult)
                .toList();

        return new ProgressionCoursResult(
                cours.getId(),
                cours.getTitre(),
                cours.getProf() != null ? cours.getProf().getNom() : null,
                cours.getProf() != null ? cours.getProf().getPrenom() : null,
                totalChapitres,
                chapitresTermines,
                ressourceRepository.countByCoursId(coursId),
                pourcentageGlobal,
                details);
    }

    public List<ProgressionCoursResult> getToutesMesProgressions(String eleveEmail) {
        return inscriptionRepository.findByEleveEmailAndStatut(eleveEmail, "VALIDE")
                .stream()
                .map(inscription ->
                        getProgressionCours(inscription.getCours().getId(), eleveEmail))
                .toList();
    }

    private Chapitre findAccessibleChapter(Long chapitreId, String eleveEmail) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapitre introuvable"));
        validateEnrollment(chapitre.getCours().getId(), eleveEmail);
        return chapitre;
    }

    private User findStudent(String eleveEmail) {
        return userRepository.findByEmail(eleveEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Etudiant introuvable"));
    }

    private void validateEnrollment(Long coursId, String eleveEmail) {
        if (!inscriptionRepository.existsByEleveEmailAndCoursIdAndStatut(
                eleveEmail,
                coursId,
                "VALIDE")) {
            throw new AccessDeniedException("Vous devez etre inscrit a ce cours");
        }
    }

    private Progression newProgression(User eleve, Chapitre chapitre) {
        Progression progression = new Progression();
        progression.setEleve(eleve);
        progression.setChapitre(chapitre);
        progression.setCours(chapitre.getCours());
        progression.demarrer();
        return progression;
    }

    private ProgressionResult toResult(Progression progression) {
        Chapitre chapitre = progression.getChapitre();
        Ressource ressource = progression.getRessource();
        return new ProgressionResult(
                progression.getId(),
                progression.getStatut(),
                progression.getPourcentage(),
                progression.getDateDebut(),
                progression.getDateMiseAJour(),
                progression.getDateFin(),
                progression.getEleve().getId(),
                progression.getEleve().getNom(),
                progression.getEleve().getPrenom(),
                progression.getCours().getId(),
                progression.getCours().getTitre(),
                chapitre != null ? chapitre.getId() : null,
                chapitre != null ? chapitre.getTitre() : null,
                ressource != null ? ressource.getId() : null,
                ressource != null ? ressource.getNom() : null);
    }
}
