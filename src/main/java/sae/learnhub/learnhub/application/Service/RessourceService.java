package sae.learnhub.learnhub.application.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sae.learnhub.learnhub.domain.dto.RessourceRequest;
import sae.learnhub.learnhub.domain.dto.RessourceResponse;
import sae.learnhub.learnhub.domain.model.Ressource;
import sae.learnhub.learnhub.domain.model.Chapitre;
import sae.learnhub.learnhub.domain.repository.RessourceRepository;
import sae.learnhub.learnhub.domain.repository.ChapitreRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RessourceService {

    private final RessourceRepository ressourceRepository;
    private final ChapitreRepository chapitreRepository;

    public RessourceResponse create(Long coursId, Long chapitreId, RessourceRequest request, String email) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId).orElse(null);
        if (chapitre == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapitre introuvable");
        }

        if (!chapitre.getCours().getId().equals(coursId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce chapitre n'appartient pas à ce cours");
        }

        if (chapitre.getCours().getProf() == null || !chapitre.getCours().getProf().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Seul le professeur responsable peut ajouter des ressources");
        }

        Ressource ressource = new Ressource();
        ressource.setNom(request.getNom());
        ressource.setUrl(request.getUrl());
        ressource.setType(request.getType());
        ressource.setTelechargeable(request.getTelechargeable());
        ressource.setChapitre(chapitre);

        Ressource savedRessource = ressourceRepository.save(ressource);
        return new RessourceResponse(
                savedRessource.getId(),
                savedRessource.getNom(),
                savedRessource.getUrl(),
                savedRessource.getType(),
                savedRessource.getTelechargeable(),
                savedRessource.getDateCreation(),
                savedRessource.getChapitre().getId(),
                savedRessource.getChapitre().getTitre());
    }

    public List<RessourceResponse> findByChapitreId(Long coursId, Long chapitreId, String profEmail) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId).orElse(null);
        if (chapitre == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapitre introuvable");
        }

        if (!chapitre.getCours().getId().equals(coursId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce chapitre n'appartient pas à ce cours");
        }

        if (profEmail != null) {
            if (chapitre.getCours().getProf() == null || !chapitre.getCours().getProf().getEmail().equals(profEmail)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Accès refusé : ce cours ne vous appartient pas");
            }
        }

        List<Ressource> ressources = ressourceRepository.findByChapitreIdOrderByNomAsc(chapitreId);
        return ressources.stream().map(ressource -> new RessourceResponse(
                ressource.getId(),
                ressource.getNom(),
                ressource.getUrl(),
                ressource.getType(),
                ressource.getTelechargeable(),
                ressource.getDateCreation(),
                ressource.getChapitre().getId(),
                ressource.getChapitre().getTitre())).toList();
    }

    public RessourceResponse update(Long coursId, Long chapitreId, Long ressourceId, RessourceRequest request,
            String email) {
        Ressource ressource = ressourceRepository.findById(ressourceId).orElse(null);
        if (ressource == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ressource introuvable");
        }

        if (!ressource.getChapitre().getId().equals(chapitreId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette ressource n'appartient pas à ce chapitre");
        }

        if (!ressource.getChapitre().getCours().getId().equals(coursId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce chapitre n'appartient pas à ce cours");
        }

        if (ressource.getChapitre().getCours().getProf() == null
                || !ressource.getChapitre().getCours().getProf().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Seul le professeur responsable peut modifier les ressources");
        }

        ressource.setNom(request.getNom());
        ressource.setUrl(request.getUrl());
        ressource.setType(request.getType());
        ressource.setTelechargeable(request.getTelechargeable());

        Ressource updatedRessource = ressourceRepository.save(ressource);
        return new RessourceResponse(
                updatedRessource.getId(),
                updatedRessource.getNom(),
                updatedRessource.getUrl(),
                updatedRessource.getType(),
                updatedRessource.getTelechargeable(),
                updatedRessource.getDateCreation(),
                updatedRessource.getChapitre().getId(),
                updatedRessource.getChapitre().getTitre());
    }

    public void delete(Long coursId, Long chapitreId, Long ressourceId, String email) {
        Ressource ressource = ressourceRepository.findById(ressourceId).orElse(null);
        if (ressource == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ressource introuvable");
        }

        if (!ressource.getChapitre().getId().equals(chapitreId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette ressource n'appartient pas à ce chapitre");
        }

        if (!ressource.getChapitre().getCours().getId().equals(coursId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce chapitre n'appartient pas à ce cours");
        }

        if (ressource.getChapitre().getCours().getProf() == null
                || !ressource.getChapitre().getCours().getProf().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Seul le professeur responsable peut supprimer les ressources");
        }

        ressourceRepository.deleteById(ressourceId);
    }
}
