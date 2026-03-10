package sae.learnhub.learnhub.application.Service;

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

    // --- Student self-enrollment ---

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

    // --- Professor enrolls a specific student ---

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

    // --- Professor views enrolled students across ALL their courses ---

    public List<Inscription> getEtudiantsPourMesCours(String profEmail) {
        return inscriptionRepository.findByCoursProf(profEmail);
    }

    // --- Professor views enrolled students for their course ---

    public List<Inscription> getEtudiantsInscrits(Long coursId, String profEmail) {
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours non trouvé"));

        if (cours.getProf() == null || !cours.getProf().getEmail().equals(profEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ce cours ne vous appartient pas");
        }

        return inscriptionRepository.findByCoursId(coursId);
    }

    // --- Professor/Admin lists all students ---

    public List<User> getAllStudents() {
        return userRepository.findAllStudents();
    }

    // --- Student history ---

    public List<Inscription> getInscriptionsParEleve(String email) {
        User eleve = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));
        return inscriptionRepository.findByEleveId(eleve.getId());
    }

    public List<Inscription> getCoursValidesParEleve(String email) {
        return inscriptionRepository.findByEleveEmailAndStatut(email, "VALIDE");
    }

    // --- Update enrollment status ---

    public Inscription changerStatutInscription(Long inscriptionId, String nouveauStatut, String emailModificateur) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscription non trouvée"));

        User modificateur = userRepository.findByEmail(emailModificateur)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));

        boolean isAdmin = "ADMINISTRATEUR".equals(modificateur.getRole());
        boolean isResponsable = inscription.getCours().getProf() != null &&
                emailModificateur.equals(inscription.getCours().getProf().getEmail());

        if (!isAdmin && !isResponsable) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Droit de modification d'accès refusé");
        }

        inscription.setStatut(nouveauStatut);
        return inscriptionRepository.save(inscription);
    }
}
