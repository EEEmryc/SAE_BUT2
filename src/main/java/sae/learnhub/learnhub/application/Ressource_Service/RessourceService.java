package sae.elearning.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sae.elearning.domain.model.Chapitre;
import sae.elearning.domain.model.Ressource;
import sae.elearning.domain.repository.ChapitreRepository;
import sae.elearning.domain.repository.InscriptionRepository;
import sae.elearning.domain.repository.RessourceRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RessourceService {

    private final RessourceRepository ressourceRepository;
    private final ChapitreRepository chapitreRepository;
    private final InscriptionRepository inscriptionRepository;

    // --- Structures de données internes ---
    public record RessourceCommand(String nom, String url, String type, Boolean telechargeable) {}
    
    public record RessourceResult(Long id, String nom, String url, String type, 
                                  Boolean telechargeable, LocalDateTime dateCreation, 
                                  Long chapitreId, String chapitreTitre) {}

    @Transactional
    public RessourceResult create(Long coursId, Long chapitreId, RessourceCommand command, String email) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId)
                .orElseThrow(() -> new IllegalArgumentException("Chapitre introuvable"));

        if (!chapitre.getCours().getId().equals(coursId)) {
            throw new IllegalArgumentException("Ce chapitre n'appartient pas à ce cours");
        }

        if (chapitre.getCours().getProf() == null || !chapitre.getCours().getProf().getEmail().equals(email)) {
            throw new SecurityException("Seul le professeur responsable peut ajouter des ressources");
        }

        Ressource ressource = new Ressource();
        ressource.setNom(command.nom());
        ressource.setUrl(command.url());
        ressource.setType(command.type());
        ressource.setTelechargeable(command.telechargeable());
        ressource.setChapitre(chapitre);
        ressource.initialiserNouveau();

        Ressource savedRessource = ressourceRepository.save(ressource);
        return toResult(savedRessource);
    }

    public List<RessourceResult> findByChapitreId(Long coursId, Long chapitreId, String profEmail, String eleveEmail) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId)
                .orElseThrow(() -> new IllegalArgumentException("Chapitre introuvable"));

        if (!chapitre.getCours().getId().equals(coursId)) {
            throw new IllegalArgumentException("Ce chapitre n'appartient pas à ce cours");
        }

        if (profEmail != null) {
            if (chapitre.getCours().getProf() == null || !chapitre.getCours().getProf().getEmail().equals(profEmail)) {
                throw new SecurityException("Accès refusé : ce cours ne vous appartient pas");
            }
        }

        if (eleveEmail != null && !inscriptionRepository.existsByEleveEmailAndCoursId(eleveEmail, coursId)) {
            throw new SecurityException("Accès refusé : vous n'êtes pas inscrit à ce cours");
        }

        return ressourceRepository.findByChapitreIdOrderByNomAsc(chapitreId)
                .stream().map(this::toResult).toList();
    }

    @Transactional
    public RessourceResult update(Long coursId, Long chapitreId, Long ressourceId, RessourceCommand command, String email) {
        Ressource ressource = ressourceRepository.findById(ressourceId)
                .orElseThrow(() -> new IllegalArgumentException("Ressource introuvable"));

        if (!ressource.getChapitre().getId().equals(chapitreId)) {
            throw new IllegalArgumentException("Cette ressource n'appartient pas à ce chapitre");
        }

        if (!ressource.getChapitre().getCours().getId().equals(coursId)) {
            throw new IllegalArgumentException("Ce chapitre n'appartient pas à ce cours");
        }

        if (ressource.getChapitre().getCours().getProf() == null
                || !ressource.getChapitre().getCours().getProf().getEmail().equals(email)) {
            throw new SecurityException("Seul le professeur responsable peut modifier les ressources");
        }

        ressource.setNom(command.nom());
        ressource.setUrl(command.url());
        ressource.setType(command.type());
        ressource.setTelechargeable(command.telechargeable());

        Ressource updatedRessource = ressourceRepository.save(ressource);
        return toResult(updatedRessource);
    }

    @Transactional
    public void delete(Long coursId, Long chapitreId, Long ressourceId, String email) {
        Ressource ressource = ressourceRepository.findById(ressourceId)
                .orElseThrow(() -> new IllegalArgumentException("Ressource introuvable"));

        if (!ressource.getChapitre().getId().equals(chapitreId)) {
            throw new IllegalArgumentException("Cette ressource n'appartient pas à ce chapitre");
        }

        if (!ressource.getChapitre().getCours().getId().equals(coursId)) {
            throw new IllegalArgumentException("Ce chapitre n'appartient pas à ce cours");
        }

        if (ressource.getChapitre().getCours().getProf() == null
                || !ressource.getChapitre().getCours().getProf().getEmail().equals(email)) {
            throw new SecurityException("Seul le professeur responsable peut supprimer les ressources");
        }

        ressourceRepository.deleteById(ressourceId);
    }

    private RessourceResult toResult(Ressource r) {
        return new RessourceResult(
                r.getId(),
                r.getNom(),
                r.getUrl(),
                r.getType(),
                r.getTelechargeable(),
                r.getDateCreation(),
                r.getChapitre().getId(),
                r.getChapitre().getTitre()
        );
    }
}