package sae.learnhub.learnhub.application.inscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sae.learnhub.learnhub.application.exception.AccessDeniedException;
import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.application.exception.ResourceNotFoundException;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.model.Inscription;
import sae.learnhub.learnhub.domain.model.InscriptionStatut;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.model.UserRole;
import sae.learnhub.learnhub.domain.repository.ICoursRepository;
import sae.learnhub.learnhub.domain.repository.IInscriptionRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class InscriptionService {

    private final IInscriptionRepository inscriptionRepository;
    private final IUserRepository userRepository;
    private final ICoursRepository coursRepository;

    @Transactional
    public Inscription inscrireEleve(Long coursId, String email) {
        User eleve = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Élève non trouvé"));

        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé"));

        String role = eleve.getRole() == null ? "" : eleve.getRole().replace("ROLE_", "");
        if (!UserRole.ETUDIANT.name().equalsIgnoreCase(role)) {
            throw new BusinessRuleException("Seul un etudiant peut demander une inscription");
        }
        boolean published = "PUBLISHED".equalsIgnoreCase(cours.getStatut())
                || "VALIDE".equalsIgnoreCase(cours.getStatut());
        if (!cours.isVisibleCatalogue() || !published) {
            throw new BusinessRuleException("Ce cours n'est pas ouvert aux inscriptions");
        }

        if (inscriptionRepository.existsByEleveIdAndCoursId(eleve.getId(), cours.getId())) {
            throw new BusinessRuleException("Déjà inscrit à ce cours");
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
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé"));

        if (cours.getProf() == null || !cours.getProf().getEmail().equals(profEmail)) {
            throw new AccessDeniedException("Ce cours ne vous appartient pas");
        }

        User eleve = userRepository.findById(eleveId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));

        String role = eleve.getRole() == null ? "" : eleve.getRole().replace("ROLE_", "");
        if (!UserRole.ETUDIANT.name().equalsIgnoreCase(role)) {
            throw new BusinessRuleException("Seul un compte étudiant peut être inscrit à un cours");
        }
        if ("INACTIF".equalsIgnoreCase(eleve.getStatut())) {
            throw new BusinessRuleException("Un compte étudiant inactif ne peut pas être inscrit");
        }

        if (inscriptionRepository.existsByEleveIdAndCoursId(eleveId, coursId)) {
            throw new BusinessRuleException("Cet étudiant est déjà inscrit à ce cours");
        }

        Inscription inscription = new Inscription();
        inscription.setEleve(eleve);
        inscription.setCours(cours);
        inscription.initialiserInscriptionValidee();
        return inscriptionRepository.save(inscription);
    }

    public List<Inscription> getEtudiantsPourMesCours(String profEmail) {
        return inscriptionRepository.findByCoursProf(profEmail);
    }

    public List<Inscription> getDemandesEnAttentePourProf(String profEmail) {
        return inscriptionRepository.findByCoursProf(profEmail).stream()
                .filter(inscription -> InscriptionStatut.EN_ATTENTE.name()
                        .equalsIgnoreCase(inscription.getStatut()))
                .toList();
    }

    public List<Inscription> getEtudiantsInscrits(Long coursId, String profEmail) {
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé"));

        if (cours.getProf() == null || !cours.getProf().getEmail().equals(profEmail)) {
            throw new AccessDeniedException("Ce cours ne vous appartient pas");
        }

        return inscriptionRepository.findByCoursId(coursId);
    }

    public List<User> getAllStudents() {
        return userRepository.findAllStudents();
    }

    public List<Inscription> getInscriptionsParEleve(String email) {
        User eleve = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        return inscriptionRepository.findByEleveId(eleve.getId());
    }

    public List<Inscription> getCoursValidesParEleve(String email) {
        return inscriptionRepository.findByEleveEmailAndStatut(email, InscriptionStatut.VALIDE.name());
    }

    @Transactional
    public Inscription changerStatutInscription(Long inscriptionId, String nouveauStatut, String profEmail) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription non trouvée"));

        if (profEmail != null) {
            if (inscription.getCours().getProf() == null
                    || !inscription.getCours().getProf().getEmail().equals(profEmail)) {
                throw new AccessDeniedException("Accès refusé");
            }
        }

        String statut = nouveauStatut == null
                ? ""
                : nouveauStatut.trim().toUpperCase(Locale.ROOT);
        if (!InscriptionStatut.VALIDE.name().equals(statut)
                && !InscriptionStatut.REFUSE.name().equals(statut)) {
            throw new BusinessRuleException(
                    "Statut invalide. Valeurs acceptées : VALIDE, REFUSE");
        }

        inscription.setStatut(statut);
        return inscriptionRepository.save(inscription);
    }

    @Transactional
    public void retirerEtudiant(Long inscriptionId, String profEmail) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription non trouvée"));
        if (inscription.getCours().getProf() == null
                || !inscription.getCours().getProf().getEmail().equals(profEmail)) {
            throw new AccessDeniedException("Ce cours ne vous appartient pas");
        }
        inscriptionRepository.deleteById(inscriptionId);
    }
}
