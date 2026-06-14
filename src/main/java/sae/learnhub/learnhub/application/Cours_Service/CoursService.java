package sae.learnhub.learnhub.application.Cours_Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import sae.learnhub.learnhub.application.exception.AccessDeniedException;
import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.application.exception.ResourceNotFoundException;
import sae.learnhub.learnhub.application.port.ResourceFileStorage;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.model.CoursStatut;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.ICoursRepository;
import sae.learnhub.learnhub.domain.repository.IInscriptionRepository;
import sae.learnhub.learnhub.domain.repository.IChapitreRepository;
import sae.learnhub.learnhub.domain.repository.IProgressionRepository;
import sae.learnhub.learnhub.domain.repository.IRessourceRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CoursService {

    private final ICoursRepository coursRepository;
    private final IUserRepository userRepository;
    private final IInscriptionRepository inscriptionRepository;
    private final IChapitreRepository chapitreRepository;
    private final IRessourceRepository ressourceRepository;
    private final IProgressionRepository progressionRepository;
    private final ResourceFileStorage fileStorage;

    // --- Structures de données internes au Service ---
    public record CoursCommand(String titre, String description, String statut, Boolean visibleCatalogue) {}

    public record CoursResult(Long id, String titre, String description, LocalDateTime dateCreation, 
                              String statut, boolean visibleCatalogue, 
                              String profNom, String profPrenom, String profEmail) {}

    public record CourseSummaryResult(long students, long chapters, long resources, int averageProgress) {}

    public CoursResult create(CoursCommand command, String email) {
        User prof = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Cours cours = new Cours();
        // Appel de la méthode métier de notre Domaine pur !
        cours.onCreate(); 
        
        cours.setTitre(command.titre().trim());
        cours.setDescription(command.description().trim());
        
        // Surcharge si on fournit des valeurs spécifiques
        if (command.statut() != null) cours.setStatut(normalizeStatus(command.statut()));
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

    public CoursResult findAccessibleById(
            Long id,
            String email,
            boolean isProfessor,
            boolean isStudent,
            boolean isAdmin) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));

        if (isAdmin) {
            return toResult(cours);
        }
        if (isProfessor && cours.getProf() != null && cours.getProf().getEmail().equals(email)) {
            return toResult(cours);
        }
        if (isStudent && inscriptionRepository.existsByEleveEmailAndCoursId(email, id)) {
            return toResult(cours);
        }
        throw new AccessDeniedException("Vous n'avez pas accès à ce cours");
    }

    public CourseSummaryResult getProfessorSummary(Long id, String email) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));
        if (cours.getProf() == null || !cours.getProf().getEmail().equals(email)) {
            throw new AccessDeniedException("Vous n'êtes pas responsable de ce cours");
        }

        List<sae.learnhub.learnhub.domain.model.Inscription> activeEnrollments =
                inscriptionRepository.findByCoursId(id).stream()
                        .filter(inscription -> "VALIDE".equalsIgnoreCase(inscription.getStatut()))
                        .toList();
        int totalChapters = chapitreRepository.findByCoursIdOrderByOrdreAsc(id).size();
        int averageProgress = activeEnrollments.isEmpty() || totalChapters == 0
                ? 0
                : (int) Math.round(activeEnrollments.stream()
                        .mapToInt(inscription -> {
                            long completed = progressionRepository
                                    .countByEleveEmailAndCoursIdAndStatut(
                                            inscription.getEleve().getEmail(),
                                            id,
                                            "TERMINE");
                            return (int) Math.min(100, (completed * 100) / totalChapters);
                        })
                        .average()
                        .orElse(0));

        return new CourseSummaryResult(
                activeEnrollments.size(),
                totalChapters,
                ressourceRepository.countByCoursId(id),
                averageProgress);
    }

    public CoursResult update(Long id, CoursCommand command, String email) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));

        if (cours.getProf() == null || !cours.getProf().getEmail().equals(email)) {
            throw new AccessDeniedException("Vous n'êtes pas responsable de ce cours");
        }

        cours.setTitre(command.titre().trim());
        cours.setDescription(command.description().trim());
        
        if (command.statut() != null) cours.setStatut(normalizeStatus(command.statut()));
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

        List<String> resourceUrls = ressourceRepository
                .findByCoursIdOrderByDateCreationDesc(id)
                .stream()
                .map(sae.learnhub.learnhub.domain.model.Ressource::getUrl)
                .toList();

        coursRepository.deleteById(id);
        resourceUrls.forEach(fileStorage::deleteByUrl);
    }

    private String normalizeStatus(String status) {
        try {
            return CoursStatut.valueOf(status.trim().toUpperCase(Locale.ROOT)).name();
        } catch (IllegalArgumentException exception) {
            throw new BusinessRuleException(
                    "Statut de cours invalide. Valeurs acceptées : DRAFT, PUBLISHED, VALIDE, ARCHIVE");
        }
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
