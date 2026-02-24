package sae.learnhub.learnhub.application.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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

    public Cours create(Cours cours, String username) {
        User prof = userRepository.findByUsername(username);
        if (prof == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé");
        }


        cours.setProfesseur(prof);
        return coursRepository.save(cours);
    }

    public List<Cours> findAll() {
        return coursRepository.findAll();
    }

    public Cours update(Long id, Cours updated, String username) {
        Cours cours = coursRepository.findById(id).orElse(null);
        if (cours == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours introuvable");
        }

        
        if (cours.getProfesseur() == null || !cours.getProfesseur().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'êtes pas responsable de ce cours");
        }

        cours.setTitre(updated.getTitre());
        cours.setDescription(updated.getDescription());
        cours.setStatut(updated.getStatut());
        cours.setVisibleCatalogue(updated.isVisibleCatalogue());

        return coursRepository.save(cours);
    }

    public void delete(Long id, String username) {
        Cours cours = coursRepository.findById(id).orElse(null);
        if (cours == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours introuvable");
        }

        if (cours.getProfesseur() == null || !cours.getProfesseur().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'êtes pas responsable de ce cours");
        }

        coursRepository.deleteById(id);
    }


}