package sae.learnhub.learnhub.application.Ressource_Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sae.learnhub.learnhub.application.exception.AccessDeniedException;
import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.application.exception.ResourceNotFoundException;
import sae.learnhub.learnhub.application.port.ResourceFileStorage;
import sae.learnhub.learnhub.domain.model.Chapitre;
import sae.learnhub.learnhub.domain.model.Ressource;
import sae.learnhub.learnhub.domain.repository.IChapitreRepository;
import sae.learnhub.learnhub.domain.repository.ICoursRepository;
import sae.learnhub.learnhub.domain.repository.IInscriptionRepository;
import sae.learnhub.learnhub.domain.repository.IRessourceRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RessourceService {

    private final IRessourceRepository ressourceRepository;
    private final IChapitreRepository chapitreRepository;
    private final ICoursRepository coursRepository;
    private final IInscriptionRepository inscriptionRepository;
    private final ResourceFileStorage fileStorage;

    private static final long MAX_FILE_SIZE = 1024L * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "xls", "xlsx", "zip",
            "mp4", "webm", "mov", "avi");

    // --- Structures de données internes ---
    public record RessourceCommand(String nom, String url, String type, Boolean telechargeable) {}
    
    public record RessourceResult(Long id, String nom, String url, String type,
                                  Boolean telechargeable, Long tailleOctets, LocalDateTime dateCreation,
                                  Long chapitreId, String chapitreTitre) {}

    @Transactional
    public RessourceResult create(Long coursId, Long chapitreId, RessourceCommand command, String email) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapitre introuvable"));

        if (!chapitre.getCours().getId().equals(coursId)) {
            throw new BusinessRuleException("Ce chapitre n'appartient pas à ce cours");
        }

        if (chapitre.getCours().getProf() == null || !chapitre.getCours().getProf().getEmail().equals(email)) {
            throw new AccessDeniedException("Seul le professeur responsable peut ajouter des ressources");
        }

        Ressource ressource = new Ressource();
        ressource.setNom(command.nom());
        ressource.setUrl(command.url());
        ressource.setType(command.type());
        ressource.setTelechargeable(command.telechargeable());
        ressource.setTailleOctets(null);
        ressource.setChapitre(chapitre);
        ressource.initialiserNouveau();

        Ressource savedRessource = ressourceRepository.save(ressource);
        return toResult(savedRessource);
    }

    @Transactional
    public RessourceResult upload(
            Long coursId,
            Long chapitreId,
            String nom,
            Boolean telechargeable,
            ResourceFileStorage.FileUpload file,
            String email) {
        validateFile(file);
        ResourceFileStorage.StoredFile storedFile = fileStorage.store(file);

        try {
            return create(
                    coursId,
                    chapitreId,
                    new RessourceCommand(
                            nom == null || nom.isBlank() ? storedFile.originalName() : nom.trim(),
                            storedFile.url(),
                            detectType(storedFile.originalName()),
                            telechargeable == null || telechargeable),
                    email,
                    storedFile.size());
        } catch (RuntimeException exception) {
            fileStorage.deleteByUrl(storedFile.url());
            throw exception;
        }
    }

    private RessourceResult create(
            Long coursId,
            Long chapitreId,
            RessourceCommand command,
            String email,
            Long tailleOctets) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapitre introuvable"));

        validateCourseAndOwner(coursId, chapitre, email);

        Ressource ressource = new Ressource();
        ressource.setNom(command.nom());
        ressource.setUrl(command.url());
        ressource.setType(command.type());
        ressource.setTelechargeable(command.telechargeable());
        ressource.setTailleOctets(tailleOctets);
        ressource.setChapitre(chapitre);
        ressource.initialiserNouveau();
        return toResult(ressourceRepository.save(ressource));
    }

    public List<RessourceResult> findByChapitreId(Long coursId, Long chapitreId, String profEmail, String eleveEmail) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapitre introuvable"));

        if (!chapitre.getCours().getId().equals(coursId)) {
            throw new BusinessRuleException("Ce chapitre n'appartient pas à ce cours");
        }

        if (profEmail != null) {
            if (chapitre.getCours().getProf() == null || !chapitre.getCours().getProf().getEmail().equals(profEmail)) {
                throw new AccessDeniedException("Accès refusé : ce cours ne vous appartient pas");
            }
        }

        if (eleveEmail != null && !inscriptionRepository.existsByEleveEmailAndCoursId(eleveEmail, coursId)) {
            throw new AccessDeniedException("Accès refusé : vous n'êtes pas inscrit à ce cours");
        }

        return ressourceRepository.findByChapitreIdOrderByNomAsc(chapitreId)
                .stream().map(this::toResult).toList();
    }

    public List<RessourceResult> findByCoursId(Long coursId, String profEmail) {
        sae.learnhub.learnhub.domain.model.Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));
        if (cours.getProf() == null || !cours.getProf().getEmail().equals(profEmail)) {
            throw new AccessDeniedException("Vous n'êtes pas responsable de ce cours");
        }
        return ressourceRepository.findByCoursIdOrderByDateCreationDesc(coursId)
                .stream()
                .map(this::toResult)
                .toList();
    }

    @Transactional
    public RessourceResult update(Long coursId, Long chapitreId, Long ressourceId, RessourceCommand command, String email) {
        Ressource ressource = ressourceRepository.findById(ressourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Ressource introuvable"));

        if (!ressource.getChapitre().getId().equals(chapitreId)) {
            throw new BusinessRuleException("Cette ressource n'appartient pas à ce chapitre");
        }

        if (!ressource.getChapitre().getCours().getId().equals(coursId)) {
            throw new BusinessRuleException("Ce chapitre n'appartient pas à ce cours");
        }

        if (ressource.getChapitre().getCours().getProf() == null
                || !ressource.getChapitre().getCours().getProf().getEmail().equals(email)) {
            throw new AccessDeniedException("Seul le professeur responsable peut modifier les ressources");
        }

        ressource.setNom(command.nom());
        ressource.setUrl(command.url());
        ressource.setType(command.type());
        ressource.setTelechargeable(command.telechargeable());

        Ressource updatedRessource = ressourceRepository.save(ressource);
        return toResult(updatedRessource);
    }

    @Transactional
    public void delete(Long coursId, Long chapitreId, Long ressourceId, String email) {
        Ressource ressource = ressourceRepository.findById(ressourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Ressource introuvable"));

        if (!ressource.getChapitre().getId().equals(chapitreId)) {
            throw new BusinessRuleException("Cette ressource n'appartient pas à ce chapitre");
        }

        if (!ressource.getChapitre().getCours().getId().equals(coursId)) {
            throw new BusinessRuleException("Ce chapitre n'appartient pas à ce cours");
        }

        if (ressource.getChapitre().getCours().getProf() == null
                || !ressource.getChapitre().getCours().getProf().getEmail().equals(email)) {
            throw new AccessDeniedException("Seul le professeur responsable peut supprimer les ressources");
        }

        ressourceRepository.deleteById(ressourceId);
        fileStorage.deleteByUrl(ressource.getUrl());
    }

    private void validateCourseAndOwner(Long coursId, Chapitre chapitre, String email) {
        if (!chapitre.getCours().getId().equals(coursId)) {
            throw new BusinessRuleException("Ce chapitre n'appartient pas à ce cours");
        }
        if (chapitre.getCours().getProf() == null
                || !chapitre.getCours().getProf().getEmail().equals(email)) {
            throw new AccessDeniedException("Seul le professeur responsable peut gérer les ressources");
        }
    }

    private void validateFile(ResourceFileStorage.FileUpload file) {
        if (file == null || file.originalName() == null || file.originalName().isBlank() || file.size() <= 0) {
            throw new BusinessRuleException("Le fichier est obligatoire");
        }
        if (file.size() > MAX_FILE_SIZE) {
            throw new BusinessRuleException("Le fichier ne doit pas dépasser 1 Go");
        }
        String extension = extensionOf(file.originalName());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
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

    private RessourceResult toResult(Ressource r) {
        return new RessourceResult(
                r.getId(),
                r.getNom(),
                r.getUrl(),
                r.getType(),
                r.getTelechargeable(),
                r.getTailleOctets(),
                r.getDateCreation(),
                r.getChapitre().getId(),
                r.getChapitre().getTitre()
        );
    }
}
