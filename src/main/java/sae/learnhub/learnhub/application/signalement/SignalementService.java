package sae.learnhub.learnhub.application.signalement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.application.exception.ResourceNotFoundException;
import sae.learnhub.learnhub.domain.model.Signalement;
import sae.learnhub.learnhub.domain.model.SignalementCategorie;
import sae.learnhub.learnhub.domain.model.SignalementStatut;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.ISignalementRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SignalementService {

    private final ISignalementRepository signalementRepository;
    private final IUserRepository userRepository;

    public record SignalementCommand(
            String sujet,
            String description,
            String categorie,
            String pieceJointeNom,
            String pieceJointeUrl
    ) {
    }

    public record SignalementResult(
            Long id,
            String sujet,
            String description,
            String categorie,
            String statut,
            LocalDateTime dateEnvoi,
            String pieceJointeNom,
            String pieceJointeUrl,
            Long auteurId,
            String auteurNom,
            String auteurPrenom,
            String auteurEmail,
            String auteurRole
    ) {
    }

    public List<SignalementResult> getMine(String email) {
        User auteur = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        return signalementRepository.findByAuteurIdOrderByDateEnvoiDesc(auteur.getId())
                .stream()
                .map(this::toResult)
                .toList();
    }

    public List<SignalementResult> getAll() {
        return signalementRepository.findAllOrderByDateEnvoiDesc()
                .stream()
                .map(this::toResult)
                .toList();
    }

    public SignalementResult getById(Long id) {
        return toResult(findById(id));
    }

    @Transactional
    public SignalementResult create(SignalementCommand command, String auteurEmail) {
        User auteur = userRepository.findByEmail(auteurEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Auteur du signalement introuvable"));

        if ("ADMIN".equalsIgnoreCase(auteur.getRole())) {
            throw new BusinessRuleException(
                    "Les signalements sont réservés aux étudiants et aux professeurs");
        }

        Signalement signalement = new Signalement();
        signalement.setSujet(command.sujet().trim());
        signalement.setDescription(command.description().trim());
        signalement.setCategorie(normalizeCategorie(command.categorie()).name());
        signalement.setPieceJointeNom(normalizeOptional(command.pieceJointeNom()));
        signalement.setPieceJointeUrl(normalizeOptional(command.pieceJointeUrl()));
        signalement.setAuteur(auteur);
        signalement.soumettre();

        return toResult(signalementRepository.save(signalement));
    }

    @Transactional
    public SignalementResult updateStatus(Long id, String statut) {
        Signalement signalement = findById(id);
        signalement.changerStatut(normalizeStatut(statut));
        return toResult(signalementRepository.save(signalement));
    }

    private Signalement findById(Long id) {
        return signalementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Signalement introuvable avec l'id : " + id));
    }

    private SignalementCategorie normalizeCategorie(String categorie) {
        try {
            return SignalementCategorie.valueOf(normalizeEnum(categorie));
        } catch (IllegalArgumentException exception) {
            throw new BusinessRuleException(
                    "Catégorie invalide. Valeurs acceptées : CONTENU, ACCES, "
                            + "COMPORTEMENT, EVALUATION, TECHNIQUE, COMPTE, MESSAGERIE, AUTRE");
        }
    }

    private SignalementStatut normalizeStatut(String statut) {
        try {
            return SignalementStatut.valueOf(normalizeEnum(statut));
        } catch (IllegalArgumentException exception) {
            throw new BusinessRuleException(
                    "Statut invalide. Valeurs acceptées : NOUVEAU, EN_COURS, TRAITE, RESOLU");
        }
    }

    private String normalizeEnum(String value) {
        return value == null
                ? ""
                : value.trim()
                        .toUpperCase(Locale.ROOT)
                        .replace('É', 'E')
                        .replace('È', 'E')
                        .replace('À', 'A')
                        .replace(' ', '_');
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private SignalementResult toResult(Signalement signalement) {
        User auteur = signalement.getAuteur();
        return new SignalementResult(
                signalement.getId(),
                signalement.getSujet(),
                signalement.getDescription(),
                signalement.getCategorie(),
                signalement.getStatut(),
                signalement.getDateEnvoi(),
                signalement.getPieceJointeNom(),
                signalement.getPieceJointeUrl(),
                auteur.getId(),
                auteur.getNom(),
                auteur.getPrenom(),
                auteur.getEmail(),
                auteur.getRole());
    }
}
