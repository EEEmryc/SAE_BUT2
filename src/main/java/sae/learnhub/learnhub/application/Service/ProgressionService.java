package sae.learnhub.learnhub.application.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sae.learnhub.learnhub.domain.dto.ProgressionCoursResponse;
import sae.learnhub.learnhub.domain.dto.ProgressionResponse;
import sae.learnhub.learnhub.domain.model.*;
import sae.learnhub.learnhub.domain.repository.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressionService {

    private final ProgressionRepository progressionRepository;
    private final UserRepository userRepository;
    private final CoursRepository coursRepository;
    private final ChapitreRepository chapitreRepository;

    /**
     * Called when a student opens a chapter.
     * Creates a row with EN_COURS / 0% if none exists yet.
     * Has no effect if the chapter is already TERMINE.
     */
    public ProgressionResponse commencerChapitre(Long chapitreId, String eleveEmail) {
        User eleve = userRepository.findByEmail(eleveEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Étudiant introuvable"));

        Chapitre chapitre = chapitreRepository.findById(chapitreId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapitre introuvable"));

        Progression progression = progressionRepository
                .findByEleveEmailAndChapitreId(eleveEmail, chapitreId)
                .orElse(new Progression());

        // Don't downgrade a completed chapter
        if ("TERMINE".equals(progression.getStatut())) {
            return toResponse(progression);
        }

        progression.setEleve(eleve);
        progression.setCours(chapitre.getCours());
        progression.setChapitre(chapitre);
        progression.setStatut("EN_COURS");
        progression.setPourcentage(0);

        return toResponse(progressionRepository.save(progression));
    }

    /**
     * Called when a student marks a chapter as done.
     * Sets TERMINE / 100%. Creates the row first if it didn't exist yet.
     */
    public ProgressionResponse terminerChapitre(Long chapitreId, String eleveEmail) {
        User eleve = userRepository.findByEmail(eleveEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Étudiant introuvable"));

        Chapitre chapitre = chapitreRepository.findById(chapitreId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapitre introuvable"));

        Progression progression = progressionRepository
                .findByEleveEmailAndChapitreId(eleveEmail, chapitreId)
                .orElse(new Progression());

        progression.setEleve(eleve);
        progression.setCours(chapitre.getCours());
        progression.setChapitre(chapitre);
        progression.setStatut("TERMINE");
        progression.setPourcentage(100);

        return toResponse(progressionRepository.save(progression));
    }

    /**
     * Returns the overall course progression for the connected student:
     * percentage = TERMINE chapters / total chapters * 100
     */
    public ProgressionCoursResponse getProgressionCours(Long coursId, String eleveEmail) {
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours introuvable"));

        int total = chapitreRepository.findByCoursIdOrderByOrdreAsc(coursId).size();
        long termines = progressionRepository.countByEleveEmailAndCoursIdAndStatut(eleveEmail, coursId, "TERMINE");

        int pourcentage = total == 0 ? 0 : (int) Math.round((double) termines / total * 100);

        List<ProgressionResponse> detail = progressionRepository
                .findByEleveEmailAndCoursId(eleveEmail, coursId)
                .stream().map(this::toResponse).toList();

        return new ProgressionCoursResponse(
                cours.getId(),
                cours.getTitre(),
                total,
                (int) termines,
                pourcentage,
                detail
        );
    }

    public List<ProgressionCoursResponse> getToutesMesProgressions(String eleveEmail) {
        // Group by distinct courses the student has started
        return progressionRepository.findByEleveEmail(eleveEmail).stream()
                .map(p -> p.getCours().getId())
                .distinct()
                .map(coursId -> getProgressionCours(coursId, eleveEmail))
                .toList();
    }

    private ProgressionResponse toResponse(Progression p) {
        return new ProgressionResponse(
                p.getId(),
                p.getStatut(),
                p.getPourcentage(),
                p.getDateDebut(),
                p.getDateMiseAJour(),
                p.getDateFin(),
                p.getEleve().getId(),
                p.getEleve().getNom(),
                p.getEleve().getPrenom(),
                p.getCours().getId(),
                p.getCours().getTitre(),
                p.getChapitre() != null ? p.getChapitre().getId() : null,
                p.getChapitre() != null ? p.getChapitre().getTitre() : null,
                p.getRessource() != null ? p.getRessource().getId() : null,
                p.getRessource() != null ? p.getRessource().getNom() : null);
    }
}
