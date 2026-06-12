package sae.learnhub.learnhub.application.Ressource_Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import sae.learnhub.learnhub.domain.model.Chapitre;
import sae.learnhub.learnhub.domain.model.Ressource;
import sae.learnhub.learnhub.domain.repository.IChapitreRepository;
import sae.learnhub.learnhub.domain.repository.IInscriptionRepository;
import sae.learnhub.learnhub.domain.repository.IRessourceRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RessourceService {

    private final IRessourceRepository ressourceRepository;
    private final IChapitreRepository chapitreRepository;
    private final IInscriptionRepository inscriptionRepository;

    // --- Structures de données internes ---
    public record RessourceCommand(String nom, String url, String type, Boolean telechargeable) {}
    
    public record RessourceResult(Long id, String nom, String url, String type, 
                                  Boolean telechargeable, LocalDateTime dateCreation, 
                                  Long chapitreId, String chapitreTitre) {}

    @Transactional
    public RessourceResult create(Long coursId, Long chapitreId, RessourceCommand command, String email) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapitre introuvable"));

        if (!chapitre.getCours().getId().equals(coursId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce chapitre n'appartient pas à ce cours");
        }

        if (chapitre.getCours().getProf() == null || !chapitre.getCours().getProf().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seul le professeur responsable peut ajouter des ressources");
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapitre introuvable"));

        if (!chapitre.getCours().getId().equals(coursId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce chapitre n'appartient pas à ce cours");
        }

        if (profEmail != null) {
            if (chapitre.getCours().getProf() == null || !chapitre.getCours().getProf().getEmail().equals(profEmail)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé : ce cours ne vous appartient pas");
            }
        }

        if (eleveEmail != null && !inscriptionRepository.existsByEleveEmailAndCoursId(eleveEmail, coursId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé : vous n'êtes pas inscrit à ce cours");
        }

        return ressourceRepository.findByChapitreIdOrderByNomAsc(chapitreId)
                .stream().map(this::toResult).toList();
    }

    @Transactional
    public RessourceResult update(Long coursId, Long chapitreId, Long ressourceId, RessourceCommand command, String email) {
        Ressource ressource = ressourceRepository.findById(ressourceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ressource introuvable"));

        if (!ressource.getChapitre().getId().equals(chapitreId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette ressource n'appartient pas à ce chapitre");
        }

        if (!ressource.getChapitre().getCours().getId().equals(coursId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce chapitre n'appartient pas à ce cours");
        }

        if (ressource.getChapitre().getCours().getProf() == null
                || !ressource.getChapitre().getCours().getProf().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seul le professeur responsable peut modifier les ressources");
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ressource introuvable"));

        if (!ressource.getChapitre().getId().equals(chapitreId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette ressource n'appartient pas à ce chapitre");
        }

        if (!ressource.getChapitre().getCours().getId().equals(coursId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce chapitre n'appartient pas à ce cours");
        }

        if (ressource.getChapitre().getCours().getProf() == null
                || !ressource.getChapitre().getCours().getProf().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seul le professeur responsable peut supprimer les ressources");
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