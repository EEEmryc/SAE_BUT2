package sae.learnhub.learnhub.application.Cours_Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import sae.learnhub.learnhub.application.exception.AccessDeniedException;
import sae.learnhub.learnhub.application.exception.ResourceNotFoundException;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.model.CoursStatut;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.ICoursRepository;
import sae.learnhub.learnhub.domain.repository.IInscriptionRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoursService {

    private final ICoursRepository coursRepository;
    private final IUserRepository userRepository;
    private final IInscriptionRepository inscriptionRepository;

    // --- Structures de données internes au Service ---
    public record CoursCommand(String titre, String description, String statut, Boolean visibleCatalogue) {}

    public record CoursResult(Long id, String titre, String description, LocalDateTime dateCreation, 
                              String statut, boolean visibleCatalogue, 
                              String profNom, String profPrenom, String profEmail) {}

    public CoursResult create(CoursCommand command, String email) {
        User prof = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Cours cours = new Cours();
        // Appel de la méthode métier de notre Domaine pur !
        cours.onCreate(); 
        
        cours.setTitre(command.titre());
        cours.setDescription(command.description());
        
        // Surcharge si on fournit des valeurs spécifiques
        if (command.statut() != null) cours.setStatut(command.statut());
        if (command.visibleCatalogue() != null) cours.setVisibleCatalogue(command.visibleCatalogue());
        
        cours.setProf(prof);

        Cours savedCours = coursRepository.save(cours);
        return toResult(savedCours);
    }

    public List<CoursResult> findAll() {
        return coursRepository.findAll().stream().map(this::toResult).toList();
    }

    public List<CoursResult> findByProfEmail(String email) {
        return coursRepository.findByProfEmail(email).stream().map(this::toResult).toList();
    }

    public List<CoursResult> getCoursValidesParProf(String email) {
        return coursRepository.findByProfEmailAndStatut(email, CoursStatut.VALIDE.name()).stream().map(this::toResult).toList();
    }

    public List<CoursResult> findByEleveEmail(String email) {
        return inscriptionRepository.findCoursByEleveEmail(email).stream().map(this::toResult).toList();
    }

    public CoursResult update(Long id, CoursCommand command, String email) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));

        if (cours.getProf() == null || !cours.getProf().getEmail().equals(email)) {
            throw new AccessDeniedException("Vous n'êtes pas responsable de ce cours");
        }

        cours.setTitre(command.titre());
        cours.setDescription(command.description());
        
        if (command.statut() != null) cours.setStatut(command.statut());
        if (command.visibleCatalogue() != null) cours.setVisibleCatalogue(command.visibleCatalogue());

        Cours updatedCours = coursRepository.save(cours);
        return toResult(updatedCours);
    }

    public void delete(Long id, String email) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));

        if (cours.getProf() == null || !cours.getProf().getEmail().equals(email)) {
            throw new AccessDeniedException("Vous n'êtes pas responsable de ce cours");
        }

        coursRepository.deleteById(id);
    }

    // --- Méthode utilitaire privée (DRY) ---
    private CoursResult toResult(Cours cours) {
        return new CoursResult(
                cours.getId(),
                cours.getTitre(),
                cours.getDescription(),
                cours.getDateCreation(),
                cours.getStatut(),
                cours.isVisibleCatalogue(),
                cours.getProf() != null ? cours.getProf().getNom() : null,
                cours.getProf() != null ? cours.getProf().getPrenom() : null,
                cours.getProf() != null ? cours.getProf().getEmail() : null
        );
    }
}
