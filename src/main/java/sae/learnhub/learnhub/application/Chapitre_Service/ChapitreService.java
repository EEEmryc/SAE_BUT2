package sae.learnhub.learnhub.application.Chapitre_Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sae.learnhub.learnhub.application.exception.AccessDeniedException;
import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.application.exception.ResourceNotFoundException;
import sae.learnhub.learnhub.application.port.ResourceFileStorage;
import sae.learnhub.learnhub.domain.model.Chapitre;
import sae.learnhub.learnhub.domain.model.Cours;
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
public class ChapitreService {

    private static final long MAX_FILE_SIZE = 1024L * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "xls", "xlsx", "zip",
            "mp4", "webm", "mov", "avi");

    private final IChapitreRepository chapitreRepository;
    private final ICoursRepository coursRepository;
    private final IInscriptionRepository inscriptionRepository;
    private final IRessourceRepository ressourceRepository;
    private final ResourceFileStorage fileStorage;

    public record ChapitreCommand(String titre, String contenu, Integer ordre) {}

    public record ChapitreResult(
            Long id,
            String titre,
            String contenu,
            Integer ordre,
            LocalDateTime dateCreation,
            String fichierPrincipalNom,
            String fichierPrincipalUrl,
            String fichierPrincipalType,
            Long fichierPrincipalTailleOctets,
            Long coursId,
            String coursTitre) {}

    public ChapitreResult create(Long coursId, ChapitreCommand command, String email) {
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));

        if (cours.getProf() == null || !cours.getProf().getEmail().equals(email)) {
            throw new AccessDeniedException("Seul le professeur responsable peut ajouter des chapitres");
        }

        Chapitre chapitre = new Chapitre();
        chapitre.setTitre(command.titre());
        chapitre.setContenu(command.contenu());
        chapitre.setOrdre(command.ordre());
        chapitre.setDateCreation(LocalDateTime.now());
        chapitre.setCours(cours);

        return toResult(chapitreRepository.save(chapitre));
    }

    public List<ChapitreResult> findByCoursId(Long coursId, String profEmail, String eleveEmail) {
        if (profEmail != null) {
            Cours cours = coursRepository.findById(coursId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));
            if (cours.getProf() == null || !cours.getProf().getEmail().equals(profEmail)) {
                throw new AccessDeniedException("Acces refuse : ce cours ne vous appartient pas");
            }
        }
        if (eleveEmail != null
                && !inscriptionRepository.existsByEleveEmailAndCoursIdAndStatut(
                        eleveEmail,
                        coursId,
                        "VALIDE")) {
            throw new AccessDeniedException("Acces refuse : vous n'etes pas inscrit a ce cours");
        }

        return chapitreRepository.findByCoursIdOrderByOrdreAsc(coursId)
                .stream()
                .map(this::toResult)
                .toList();
    }

    public ChapitreResult update(
            Long coursId,
            Long chapitreId,
            ChapitreCommand command,
            String email) {
        Chapitre chapitre = findOwnedChapter(coursId, chapitreId, email);
        chapitre.setTitre(command.titre());
        chapitre.setContenu(command.contenu());
        chapitre.setOrdre(command.ordre());
        return toResult(chapitreRepository.save(chapitre));
    }

    public ChapitreResult uploadMainFile(
            Long coursId,
            Long chapitreId,
            ResourceFileStorage.FileUpload file,
            String email) {
        Chapitre chapitre = findOwnedChapter(coursId, chapitreId, email);
        validateFile(file);
        String previousUrl = chapitre.getFichierPrincipalUrl();
        ResourceFileStorage.StoredFile storedFile = fileStorage.store(file);

        try {
            chapitre.setFichierPrincipalNom(storedFile.originalName());
            chapitre.setFichierPrincipalUrl(storedFile.url());
            chapitre.setFichierPrincipalType(detectType(storedFile.originalName()));
            chapitre.setFichierPrincipalTailleOctets(storedFile.size());
            Chapitre saved = chapitreRepository.save(chapitre);
            fileStorage.deleteByUrl(previousUrl);
            return toResult(saved);
        } catch (RuntimeException exception) {
            fileStorage.deleteByUrl(storedFile.url());
            throw exception;
        }
    }

    public ChapitreResult deleteMainFile(Long coursId, Long chapitreId, String email) {
        Chapitre chapitre = findOwnedChapter(coursId, chapitreId, email);
        String previousUrl = chapitre.getFichierPrincipalUrl();
        chapitre.setFichierPrincipalNom(null);
        chapitre.setFichierPrincipalUrl(null);
        chapitre.setFichierPrincipalType(null);
        chapitre.setFichierPrincipalTailleOctets(null);
        Chapitre saved = chapitreRepository.save(chapitre);
        fileStorage.deleteByUrl(previousUrl);
        return toResult(saved);
    }

    public void delete(Long coursId, Long chapitreId, String email) {
        Chapitre chapitre = findOwnedChapter(coursId, chapitreId, email);
        List<String> resourceUrls = ressourceRepository
                .findByChapitreIdOrderByNomAsc(chapitreId)
                .stream()
                .map(sae.learnhub.learnhub.domain.model.Ressource::getUrl)
                .toList();

        chapitreRepository.deleteById(chapitreId);
        resourceUrls.forEach(fileStorage::deleteByUrl);
        fileStorage.deleteByUrl(chapitre.getFichierPrincipalUrl());
    }

    private Chapitre findOwnedChapter(Long coursId, Long chapitreId, String email) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapitre introuvable"));
        if (!chapitre.getCours().getId().equals(coursId)) {
            throw new BusinessRuleException("Ce chapitre n'appartient pas a ce cours");
        }
        if (chapitre.getCours().getProf() == null
                || !chapitre.getCours().getProf().getEmail().equals(email)) {
            throw new AccessDeniedException(
                    "Seul le professeur responsable peut gerer les chapitres");
        }
        return chapitre;
    }

    private void validateFile(ResourceFileStorage.FileUpload file) {
        if (file == null || file.originalName() == null
                || file.originalName().isBlank() || file.size() <= 0) {
            throw new BusinessRuleException("Le fichier est obligatoire");
        }
        if (file.size() > MAX_FILE_SIZE) {
            throw new BusinessRuleException("Le fichier ne doit pas depasser 1 Go");
        }
        if (!ALLOWED_EXTENSIONS.contains(extensionOf(file.originalName()))) {
            throw new BusinessRuleException(
                    "Format non autorise. Formats acceptes : PDF, Word, Excel, ZIP et video");
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

    private ChapitreResult toResult(Chapitre chapitre) {
        return new ChapitreResult(
                chapitre.getId(),
                chapitre.getTitre(),
                chapitre.getContenu(),
                chapitre.getOrdre(),
                chapitre.getDateCreation(),
                chapitre.getFichierPrincipalNom(),
                chapitre.getFichierPrincipalUrl(),
                chapitre.getFichierPrincipalType(),
                chapitre.getFichierPrincipalTailleOctets(),
                chapitre.getCours().getId(),
                chapitre.getCours().getTitre());
    }
}
