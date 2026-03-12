package sae.learnhub.learnhub.application.Inscriptions_Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.model.Inscription;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.CoursRepository;
import sae.learnhub.learnhub.domain.repository.InscriptionRepository;
import sae.learnhub.learnhub.domain.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final UserRepository userRepository;
    private final CoursRepository coursRepository;

    public Inscription inscrireEleve(Long coursId, String email) {
        User eleve = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Élève non trouvé"));

        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours non trouvé"));

        if (inscriptionRepository.existsByEleveIdAndCoursId(eleve.getId(), cours.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Déjà inscrit à ce cours");
        }

        Inscription inscription = new Inscription();
        inscription.setEleve(eleve);
        inscription.setCours(cours);
        return inscriptionRepository.save(inscription);
    }

    public Inscription inscrireEleveParProfesseur(Long coursId, Long eleveId, String profEmail) {
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours non trouvé"));

        if (cours.getProf() == null || !cours.getProf().getEmail().equals(profEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ce cours ne vous appartient pas");
        }

        User eleve = userRepository.findById(eleveId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Étudiant non trouvé"));

        if (inscriptionRepository.existsByEleveIdAndCoursId(eleveId, coursId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cet étudiant est déjà inscrit à ce cours");
        }

        Inscription inscription = new Inscription();
        inscription.setEleve(eleve);
        inscription.setCours(cours);
        return inscriptionRepository.save(inscription);
    }

    public List<Inscription> getEtudiantsPourMesCours(String profEmail) {
        return inscriptionRepository.findByCoursProf(profEmail);
    }

    public List<Inscription> getEtudiantsInscrits(Long coursId, String profEmail) {
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours non trouvé"));

        if (cours.getProf() == null || !cours.getProf().getEmail().equals(profEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ce cours ne vous appartient pas");
        }

        return inscriptionRepository.findByCoursId(coursId);
    }

    public List<User> getAllStudents() {
        return userRepository.findAllStudents();
    }

    public List<Inscription> getInscriptionsParEleve(String email) {
        User eleve = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));
        return inscriptionRepository.findByEleveId(eleve.getId());
    }

    public List<Inscription> getCoursValidesParEleve(String email) {
        return inscriptionRepository.findByEleveEmailAndStatut(email, "VALIDE");
    }

    public Inscription changerStatutInscription(Long inscriptionId, String nouveauStatut) {
        return changerStatutInscription(inscriptionId, nouveauStatut, null);
    }

    public Inscription changerStatutInscription(Long inscriptionId, String nouveauStatut, String profEmail) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscription non trouvée"));

        if (profEmail != null) {
            // Only the responsible professor of the course can change the status
            if (inscription.getCours().getProf() == null
                    || !inscription.getCours().getProf().getEmail().equals(profEmail)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
            }
        }

        inscription.setStatut(nouveauStatut);
        return inscriptionRepository.save(inscription);
    }
}
