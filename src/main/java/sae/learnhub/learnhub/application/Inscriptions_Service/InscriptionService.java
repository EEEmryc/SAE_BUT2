package sae.elearning.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sae.elearning.domain.model.Cours;
import sae.elearning.domain.model.Inscription;
import sae.elearning.domain.model.User;
import sae.elearning.domain.repository.CoursRepository;
import sae.elearning.domain.repository.InscriptionRepository;
import sae.elearning.domain.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final UserRepository userRepository;
    private final CoursRepository coursRepository;

    @Transactional
    public Inscription inscrireEleve(Long coursId, String email) {
        User eleve = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Élève non trouvé"));

        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new IllegalArgumentException("Cours non trouvé"));

        if (inscriptionRepository.existsByEleveIdAndCoursId(eleve.getId(), cours.getId())) {
            throw new IllegalArgumentException("Déjà inscrit à ce cours");
        }

        Inscription inscription = new Inscription();
        inscription.setEleve(eleve);
        inscription.setCours(cours);
        inscription.initialiserNouvelleInscription();
        return inscriptionRepository.save(inscription);
    }

    @Transactional
    public Inscription inscrireEleveParProfesseur(Long coursId, Long eleveId, String profEmail) {
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new IllegalArgumentException("Cours non trouvé"));

        if (cours.getProf() == null || !cours.getProf().getEmail().equals(profEmail)) {
            throw new SecurityException("Ce cours ne vous appartient pas");
        }

        User eleve = userRepository.findById(eleveId)
                .orElseThrow(() -> new IllegalArgumentException("Étudiant non trouvé"));

        if (inscriptionRepository.existsByEleveIdAndCoursId(eleveId, coursId)) {
            throw new IllegalArgumentException("Cet étudiant est déjà inscrit à ce cours");
        }

        Inscription inscription = new Inscription();
        inscription.setEleve(eleve);
        inscription.setCours(cours);
        inscription.initialiserNouvelleInscription();
        return inscriptionRepository.save(inscription);
    }

    public List<Inscription> getEtudiantsPourMesCours(String profEmail) {
        return inscriptionRepository.findByCoursProf(profEmail);
    }

    public List<Inscription> getEtudiantsInscrits(Long coursId, String profEmail) {
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new IllegalArgumentException("Cours non trouvé"));

        if (cours.getProf() == null || !cours.getProf().getEmail().equals(profEmail)) {
            throw new SecurityException("Ce cours ne vous appartient pas");
        }

        return inscriptionRepository.findByCoursId(coursId);
    }

    public List<User> getAllStudents() {
        return userRepository.findAllStudents();
    }

    public List<Inscription> getInscriptionsParEleve(String email) {
        User eleve = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        return inscriptionRepository.findByEleveId(eleve.getId());
    }

    public List<Inscription> getCoursValidesParEleve(String email) {
        return inscriptionRepository.findByEleveEmailAndStatut(email, "VALIDE");
    }

    @Transactional
    public Inscription changerStatutInscription(Long inscriptionId, String nouveauStatut, String profEmail) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Inscription non trouvée"));

        if (profEmail != null) {
            if (inscription.getCours().getProf() == null
                    || !inscription.getCours().getProf().getEmail().equals(profEmail)) {
                throw new SecurityException("Accès refusé");
            }
        }

        inscription.setStatut(nouveauStatut);
        return inscriptionRepository.save(inscription);
    }
}