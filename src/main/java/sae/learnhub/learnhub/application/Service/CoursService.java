package sae.learnhub.learnhub.application.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sae.learnhub.learnhub.domain.dto.CoursRequest;
import sae.learnhub.learnhub.domain.dto.CoursResponse;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.CoursRepository;
import sae.learnhub.learnhub.domain.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoursService {

    private final CoursRepository coursRepository;
    private final UserRepository userRepository;

    public CoursResponse create(CoursRequest request, String email) {
        User prof = userRepository.findByEmail(email).orElse(null);
        if (prof == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé");
        }

        Cours cours = new Cours();
        cours.setTitre(request.getTitre());
        cours.setDescription(request.getDescription());
        cours.setStatut(request.getStatut());
        cours.setVisibleCatalogue(request.getVisibleCatalogue());
        cours.setProf(prof);

        Cours savedCours = coursRepository.save(cours);
        return new CoursResponse(savedCours.getId(), savedCours.getTitre(), savedCours.getDescription(),
                savedCours.getDateCreation(), savedCours.getStatut(), savedCours.isVisibleCatalogue(),
                prof.getNom(), prof.getPrenom(), prof.getEmail());
    }

    public List<CoursResponse> findAllResponses() {
        List<Cours> coursList = coursRepository.findAll();
        return coursList.stream()
                .map(cours -> new CoursResponse(cours.getId(), cours.getTitre(), cours.getDescription(),
                        cours.getDateCreation(), cours.getStatut(), cours.isVisibleCatalogue(),
                        cours.getProf() != null ? cours.getProf().getNom() : null,
                        cours.getProf() != null ? cours.getProf().getPrenom() : null,
                        cours.getProf() != null ? cours.getProf().getEmail() : null))
                .toList();
    }

    public CoursResponse update(Long id, CoursRequest request, String email) {
        Cours cours = coursRepository.findById(id).orElse(null);
        if (cours == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours introuvable");
        }

        if (cours.getProf() == null || !cours.getProf().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'êtes pas responsable de ce cours");
        }

        cours.setTitre(request.getTitre());
        cours.setDescription(request.getDescription());
        cours.setStatut(request.getStatut());
        cours.setVisibleCatalogue(request.getVisibleCatalogue());

        Cours updatedCours = coursRepository.save(cours);
        return new CoursResponse(updatedCours.getId(), updatedCours.getTitre(), updatedCours.getDescription(),
                updatedCours.getDateCreation(), updatedCours.getStatut(), updatedCours.isVisibleCatalogue(),
                updatedCours.getProf().getNom(), updatedCours.getProf().getPrenom(), updatedCours.getProf().getEmail());
    }

    public void delete(Long id, String email) {
        Cours cours = coursRepository.findById(id).orElse(null);
        if (cours == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours introuvable");
        }

        if (cours.getProf() == null || !cours.getProf().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'êtes pas responsable de ce cours");
        }

        coursRepository.deleteById(id);
    }
}
