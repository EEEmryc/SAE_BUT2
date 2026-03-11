package sae.learnhub.learnhub.application.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import sae.learnhub.learnhub.api.dto.ChapitreRequest;
import sae.learnhub.learnhub.api.dto.ChapitreResponse;
import sae.learnhub.learnhub.domain.model.Chapitre;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.repository.ChapitreRepository;
import sae.learnhub.learnhub.domain.repository.CoursRepository;
import sae.learnhub.learnhub.domain.repository.InscriptionRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChapitreService {

    private final ChapitreRepository chapitreRepository;
    private final CoursRepository coursRepository;
    private final InscriptionRepository inscriptionRepository;

    public ChapitreResponse create(Long coursId, ChapitreRequest request, String email) {
        Cours cours = coursRepository.findById(coursId).orElse(null);
        if (cours == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours introuvable");
        }

        if (cours.getProf() == null || !cours.getProf().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Seul le professeur responsable peut ajouter des chapitres");
        }

        Chapitre chapitre = new Chapitre();
        chapitre.setTitre(request.getTitre());
        chapitre.setContenu(request.getContenu());
        chapitre.setOrdre(request.getOrdre());
        chapitre.setCours(cours);

        Chapitre savedChapitre = chapitreRepository.save(chapitre);
        return new ChapitreResponse(
                savedChapitre.getId(),
                savedChapitre.getTitre(),
                savedChapitre.getContenu(),
                savedChapitre.getOrdre(),
                savedChapitre.getDateCreation(),
                savedChapitre.getCours().getId(),
                savedChapitre.getCours().getTitre());
    }

    public List<ChapitreResponse> findByCoursId(Long coursId, String profEmail, String eleveEmail) {
        if (profEmail != null) {
            Cours cours = coursRepository.findById(coursId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours introuvable"));
            if (cours.getProf() == null || !cours.getProf().getEmail().equals(profEmail)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Accès refusé : ce cours ne vous appartient pas");
            }
        }
        if (eleveEmail != null && !inscriptionRepository.existsByEleveEmailAndCoursId(eleveEmail, coursId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Accès refusé : vous n'êtes pas inscrit à ce cours");
        }
        List<Chapitre> chapitres = chapitreRepository.findByCoursIdOrderByOrdreAsc(coursId);
        return chapitres.stream().map(chapitre -> new ChapitreResponse(
                chapitre.getId(),
                chapitre.getTitre(),
                chapitre.getContenu(),
                chapitre.getOrdre(),
                chapitre.getDateCreation(),
                chapitre.getCours().getId(),
                chapitre.getCours().getTitre())).toList();
    }

    public ChapitreResponse update(Long coursId, Long chapitreId, ChapitreRequest request, String email) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId).orElse(null);
        if (chapitre == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapitre introuvable");
        }

        if (!chapitre.getCours().getId().equals(coursId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce chapitre n'appartient pas à ce cours");
        }

        if (chapitre.getCours().getProf() == null || !chapitre.getCours().getProf().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Seul le professeur responsable peut modifier les chapitres");
        }

        chapitre.setTitre(request.getTitre());
        chapitre.setContenu(request.getContenu());
        chapitre.setOrdre(request.getOrdre());

        Chapitre updatedChapitre = chapitreRepository.save(chapitre);
        return new ChapitreResponse(
                updatedChapitre.getId(),
                updatedChapitre.getTitre(),
                updatedChapitre.getContenu(),
                updatedChapitre.getOrdre(),
                updatedChapitre.getDateCreation(),
                updatedChapitre.getCours().getId(),
                updatedChapitre.getCours().getTitre());
    }

    public void delete(Long coursId, Long chapitreId, String email) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId).orElse(null);
        if (chapitre == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapitre introuvable");
        }

        if (!chapitre.getCours().getId().equals(coursId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce chapitre n'appartient pas à ce cours");
        }

        if (chapitre.getCours().getProf() == null || !chapitre.getCours().getProf().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Seul le professeur responsable peut supprimer les chapitres");
        }

        chapitreRepository.deleteById(chapitreId);
    }
}
