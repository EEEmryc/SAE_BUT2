package sae.learnhub.learnhub.application.Progressions_Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sae.learnhub.learnhub.domain.model.*;
import sae.learnhub.learnhub.domain.repository.*;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProgressionService {

    private final ProgressionRepository progressionRepository;
    private final UserRepository userRepository;
    private final ChapitreRepository chapitreRepository;

    public record ProgressionResult(
        Long id, String statut, Integer pourcentage, 
        LocalDateTime dateDebut, LocalDateTime dateMiseAJour, LocalDateTime dateFin,
        Long eleveId, String eleveNom, Long coursId, String coursTitre
    ) {}

    @Transactional
    public ProgressionResult commencerChapitre(Long chapitreId, String eleveEmail) {
        User eleve = userRepository.findByEmail(eleveEmail)
            .orElseThrow(() -> new IllegalArgumentException("Étudiant introuvable"));

        Chapitre chapitre = chapitreRepository.findById(chapitreId)
            .orElseThrow(() -> new IllegalArgumentException("Chapitre introuvable"));

        // Logique métier isolée
        Progression progression = progressionRepository.findByEleveIdAndChapitreId(eleve.getId(), chapitreId)
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

    private ProgressionResult toResult(Progression p) {
        return new ProgressionResult(
            p.getId(), p.getStatut(), p.getPourcentage(),
            p.getDateDebut(), p.getDateMiseAJour(), p.getDateFin(),
            p.getEleve().getId(), p.getEleve().getNom(),
            p.getCours().getId(), p.getCours().getTitre()
        );
    }
}