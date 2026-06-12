package sae.learnhub.learnhub.application.Progressions_Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sae.learnhub.learnhub.application.exception.ResourceNotFoundException;
import sae.learnhub.learnhub.domain.model.*;
import sae.learnhub.learnhub.domain.repository.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressionService {

    private final IProgressionRepository progressionRepository;
    private final IUserRepository userRepository;
    private final ICoursRepository coursRepository;
    private final IChapitreRepository chapitreRepository;

    public record ProgressionResult(
        Long id, String statut, Integer pourcentage,
        LocalDateTime dateDebut, LocalDateTime dateMiseAJour, LocalDateTime dateFin,
        Long eleveId, String eleveNom, String elevePrenom,
        Long coursId, String coursTitre,
        Long chapitreId, String chapitreTitre,
        Long ressourceId, String ressourceNom
    ) {}

    public record ProgressionCoursResult(
        Long coursId, String coursTitre, Integer totalChapitres, Integer chapitresTermines,
        Integer pourcentageGlobal, List<ProgressionResult> details
    ) {}

    @Transactional
    public ProgressionResult commencerChapitre(Long chapitreId, String eleveEmail) {
        User eleve = userRepository.findByEmail(eleveEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable"));

        Chapitre chapitre = chapitreRepository.findById(chapitreId)
            .orElseThrow(() -> new ResourceNotFoundException("Chapitre introuvable"));

        Progression progression = progressionRepository.findByEleveEmailAndChapitreId(eleveEmail, chapitreId)
            .orElseGet(() -> {
                Progression p = new Progression();
                p.setEleve(eleve);
                p.setChapitre(chapitre);
                p.setCours(chapitre.getCours());
                p.demarrer();
                return p;
            });

        return toResult(progressionRepository.save(progression));
    }

    @Transactional
    public ProgressionResult terminerChapitre(Long chapitreId, String eleveEmail) {
        Progression progression = progressionRepository.findByEleveEmailAndChapitreId(eleveEmail, chapitreId)
            .orElseThrow(() -> new ResourceNotFoundException("Progression introuvable pour ce chapitre"));

        progression.terminer();

        return toResult(progressionRepository.save(progression));
    }

    public ProgressionCoursResult getProgressionCours(Long coursId, String eleveEmail) {
        Cours cours = coursRepository.findById(coursId)
            .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));

        int totalChapitres = chapitreRepository.findByCoursIdOrderByOrdreAsc(coursId).size();
        int chapitresTermines = (int) progressionRepository.countByEleveEmailAndCoursIdAndStatut(eleveEmail, coursId, ProgressionStatut.TERMINE.name());
        int pourcentageGlobal = totalChapitres == 0 ? 0 : (chapitresTermines * 100) / totalChapitres;

        List<ProgressionResult> details = progressionRepository.findByEleveEmailAndCoursId(eleveEmail, coursId)
            .stream().map(this::toResult).toList();

        return new ProgressionCoursResult(cours.getId(), cours.getTitre(), totalChapitres, chapitresTermines, pourcentageGlobal, details);
    }

    public List<ProgressionCoursResult> getToutesMesProgressions(String eleveEmail) {
        List<Progression> progressions = progressionRepository.findByEleveEmail(eleveEmail);

        Map<Long, String> coursParId = progressions.stream()
            .collect(Collectors.toMap(p -> p.getCours().getId(), p -> p.getCours().getTitre(), (a, b) -> a));

        return coursParId.keySet().stream()
            .map(coursId -> getProgressionCours(coursId, eleveEmail))
            .toList();
    }

    private ProgressionResult toResult(Progression p) {
        Chapitre chapitre = p.getChapitre();
        Ressource ressource = p.getRessource();
        return new ProgressionResult(
            p.getId(), p.getStatut(), p.getPourcentage(),
            p.getDateDebut(), p.getDateMiseAJour(), p.getDateFin(),
            p.getEleve().getId(), p.getEleve().getNom(), p.getEleve().getPrenom(),
            p.getCours().getId(), p.getCours().getTitre(),
            chapitre != null ? chapitre.getId() : null,
            chapitre != null ? chapitre.getTitre() : null,
            ressource != null ? ressource.getId() : null,
            ressource != null ? ressource.getNom() : null
        );
    }
}
