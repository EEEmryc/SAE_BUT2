package sae.learnhub.learnhub.application.cours;

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
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private static final long MAX_FILE_SIZE = 1024L * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "xls", "xlsx", "zip",
            "mp4", "webm", "mov", "avi");

    public record CoursResult(Long id, String titre, String description, LocalDateTime dateCreation,
                              String statut, boolean visibleCatalogue,
                              String fichierPrincipalNom, String fichierPrincipalUrl,
                              String fichierPrincipalType, Long fichierPrincipalTailleOctets,
                              String profNom, String profPrenom, String profEmail) {}

    public record CourseSummaryResult(long students, long chapters, long resources, int averageProgress) {}

    public record CourseCatalogResult(
            Long id,
            String titre,
            String description,
            String statut,
            String profNom,
            String profPrenom,
            String profEmail,
            int nombreChapitres,
            long nombreRessources,
            String statutInscription) {}

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
        return inscriptionRepository.findByEleveEmailAndStatut(email, "VALIDE")
                .stream()
                .map(sae.learnhub.learnhub.domain.model.Inscription::getCours)
                .map(this::toResult)
                .toList();
    }

    public List<CourseCatalogResult> getCatalogue(String email) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouve"));
        Map<Long, sae.learnhub.learnhub.domain.model.Inscription> enrollments =
                inscriptionRepository.findByEleveId(student.getId()).stream()
                        .collect(Collectors.toMap(
                                inscription -> inscription.getCours().getId(),
                                Function.identity(),
                                (first, second) -> first));

        return coursRepository
                .findVisibleCatalogueByStatuts(List.of("PUBLISHED", "VALIDE"))
                .stream()
                .map(cours -> {
                    sae.learnhub.learnhub.domain.model.Inscription enrollment =
                            enrollments.get(cours.getId());
                    return new CourseCatalogResult(
                            cours.getId(),
                            cours.getTitre(),
                            cours.getDescription(),
                            cours.getStatut(),
                            cours.getProf() != null ? cours.getProf().getNom() : null,
                            cours.getProf() != null ? cours.getProf().getPrenom() : null,
                            cours.getProf() != null ? cours.getProf().getEmail() : null,
                            chapitreRepository.findByCoursIdOrderByOrdreAsc(cours.getId()).size(),
                            ressourceRepository.countByCoursId(cours.getId()),
                            enrollment != null ? enrollment.getStatut() : null);
                })
                .toList();
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
        if (isStudent && inscriptionRepository.existsByEleveEmailAndCoursIdAndStatut(
                email,
                id,
                "VALIDE")) {
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

    public CoursResult uploadMainFile(
            Long id,
            ResourceFileStorage.FileUpload file,
            String email) {
        Cours cours = findOwnedCourse(id, email);
        validateFile(file);
        String previousUrl = cours.getFichierPrincipalUrl();
        ResourceFileStorage.StoredFile storedFile = fileStorage.store(file);

        try {
            cours.setFichierPrincipalNom(storedFile.originalName());
            cours.setFichierPrincipalUrl(storedFile.url());
            cours.setFichierPrincipalType(detectType(storedFile.originalName()));
            cours.setFichierPrincipalTailleOctets(storedFile.size());
            Cours saved = coursRepository.save(cours);
            fileStorage.deleteByUrl(previousUrl);
            return toResult(saved);
        } catch (RuntimeException exception) {
            fileStorage.deleteByUrl(storedFile.url());
            throw exception;
        }
    }

    public CoursResult deleteMainFile(Long id, String email) {
        Cours cours = findOwnedCourse(id, email);
        String previousUrl = cours.getFichierPrincipalUrl();
        cours.setFichierPrincipalNom(null);
        cours.setFichierPrincipalUrl(null);
        cours.setFichierPrincipalType(null);
        cours.setFichierPrincipalTailleOctets(null);
        Cours saved = coursRepository.save(cours);
        fileStorage.deleteByUrl(previousUrl);
        return toResult(saved);
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
        List<String> chapterFileUrls = chapitreRepository
                .findByCoursIdOrderByOrdreAsc(id)
                .stream()
                .map(sae.learnhub.learnhub.domain.model.Chapitre::getFichierPrincipalUrl)
                .toList();
        String mainFileUrl = cours.getFichierPrincipalUrl();

        coursRepository.deleteById(id);
        resourceUrls.forEach(fileStorage::deleteByUrl);
        chapterFileUrls.forEach(fileStorage::deleteByUrl);
        fileStorage.deleteByUrl(mainFileUrl);
    }

    private String normalizeStatus(String status) {
        try {
            return CoursStatut.valueOf(status.trim().toUpperCase(Locale.ROOT)).name();
        } catch (IllegalArgumentException exception) {
            throw new BusinessRuleException(
                    "Statut de cours invalide. Valeurs acceptées : DRAFT, PUBLISHED, VALIDE, ARCHIVE");
        }
    }

    private Cours findOwnedCourse(Long id, String email) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));
        if (cours.getProf() == null || !cours.getProf().getEmail().equals(email)) {
            throw new AccessDeniedException("Vous n'êtes pas responsable de ce cours");
        }
        return cours;
    }

    private void validateFile(ResourceFileStorage.FileUpload file) {
        if (file == null || file.originalName() == null
                || file.originalName().isBlank() || file.size() <= 0) {
            throw new BusinessRuleException("Le fichier est obligatoire");
        }
        if (file.size() > MAX_FILE_SIZE) {
            throw new BusinessRuleException("Le fichier ne doit pas dépasser 1 Go");
        }
        if (!ALLOWED_EXTENSIONS.contains(extensionOf(file.originalName()))) {
            throw new BusinessRuleException(
                    "Format non autorisé. Formats acceptés : PDF, Word, Excel, ZIP et vidéo");
        }
    }

    private String detectType(String fileName) {
        return switch (extensionOf(fileName)) {
            case "pdf" -> "PDF";
            case "doc", "docx" -> "WORD";
            case "xls", "xlsx" -> "EXCEL";
            case "zip" -> "ZIP";
            case "mp4", "webm", "mov", "avi" -> "VIDEO";
            default -> "OTHER";
        };
    }

    private String extensionOf(String fileName) {
        int separator = fileName.lastIndexOf('.');
        return separator < 0
                ? ""
                : fileName.substring(separator + 1).toLowerCase(Locale.ROOT);
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
                cours.getFichierPrincipalNom(),
                cours.getFichierPrincipalUrl(),
                cours.getFichierPrincipalType(),
                cours.getFichierPrincipalTailleOctets(),
                cours.getProf() != null ? cours.getProf().getNom() : null,
                cours.getProf() != null ? cours.getProf().getPrenom() : null,
                cours.getProf() != null ? cours.getProf().getEmail() : null
        );
    }
}
