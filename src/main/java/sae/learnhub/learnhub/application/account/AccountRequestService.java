package sae.learnhub.learnhub.application.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.application.exception.ResourceNotFoundException;
import sae.learnhub.learnhub.application.port.AccountRequestNotificationSender;
import sae.learnhub.learnhub.domain.model.AccountRequest;
import sae.learnhub.learnhub.domain.model.AccountRequestStatus;
import sae.learnhub.learnhub.domain.model.UserRole;
import sae.learnhub.learnhub.domain.repository.IAccountRequestRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountRequestService {

    private static final Set<String> REQUESTABLE_ROLES = Set.of(
            UserRole.ETUDIANT.name(),
            UserRole.PROFESSEUR.name());

    private final IAccountRequestRepository requestRepository;
    private final IUserRepository userRepository;
    private final AccountRequestNotificationSender notificationSender;

    public record SubmitCommand(
            String nom,
            String prenom,
            String email,
            String formation,
            String requestedRole,
            String commentaire
    ) {
    }

    public record RequestResult(
            Long id,
            String nom,
            String prenom,
            String email,
            String formation,
            String requestedRole,
            String commentaire,
            String statut,
            LocalDateTime dateCreation,
            LocalDateTime dateTraitement,
            boolean confirmationEmailSent
    ) {
    }

    @Transactional
    public RequestResult submit(SubmitCommand command) {
        String email = normalizeEmail(command.email());
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessRuleException(
                    "Un compte existe déjà avec cette adresse email");
        }
        if (requestRepository.existsByEmailAndStatus(
                email, AccountRequestStatus.EN_ATTENTE.name())) {
            throw new BusinessRuleException(
                    "Une demande est déjà en attente pour cette adresse email");
        }

        AccountRequest request = new AccountRequest();
        request.setNom(command.nom().trim());
        request.setPrenom(command.prenom().trim());
        request.setEmail(email);
        request.setFormation(command.formation().trim());
        request.setRequestedRole(normalizeRequestedRole(command.requestedRole()));
        request.setCommentaire(command.commentaire().trim());
        request.soumettre();

        AccountRequest saved = requestRepository.save(request);
        boolean emailSent = notificationSender.sendRequestReceived(
                saved.getEmail(), saved.getPrenom());
        return toResult(saved, emailSent);
    }

    public List<RequestResult> findAll(String status) {
        List<AccountRequest> requests = status == null || status.isBlank()
                ? requestRepository.findAllOrderByDateCreationDesc()
                : requestRepository.findByStatusOrderByDateCreationDesc(
                        normalizeStatus(status));
        return requests.stream().map(request -> toResult(request, false)).toList();
    }

    public RequestResult findById(Long id) {
        return toResult(getRequired(id), false);
    }

    @Transactional
    public RequestResult updateStatus(Long id, String status) {
        AccountRequest request = getRequired(id);
        AccountRequestStatus targetStatus = parseDecisionStatus(status);

        if (!AccountRequestStatus.EN_ATTENTE.name().equals(request.getStatut())) {
            throw new BusinessRuleException("Cette demande a déjà été traitée");
        }

        boolean emailSent = false;
        if (targetStatus == AccountRequestStatus.ACCEPTEE) {
            request.accepter();
        } else {
            request.refuser();
            emailSent = notificationSender.sendRequestRejected(
                    request.getEmail(), request.getPrenom());
        }

        return toResult(requestRepository.save(request), emailSent);
    }

    private AccountRequest getRequired(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Demande de compte introuvable"));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeRequestedRole(String role) {
        String normalized = role == null
                ? ""
                : role.trim().toUpperCase(Locale.ROOT).replaceFirst("^ROLE_", "");
        if (!REQUESTABLE_ROLES.contains(normalized)) {
            throw new BusinessRuleException(
                    "Type de compte invalide. Valeurs acceptées : ETUDIANT, PROFESSEUR");
        }
        return normalized;
    }

    private String normalizeStatus(String status) {
        try {
            return AccountRequestStatus.valueOf(
                    status.trim().toUpperCase(Locale.ROOT)).name();
        } catch (IllegalArgumentException exception) {
            throw new BusinessRuleException("Statut de demande invalide");
        }
    }

    private AccountRequestStatus parseDecisionStatus(String status) {
        AccountRequestStatus parsed = AccountRequestStatus.valueOf(
                normalizeStatus(status));
        if (parsed != AccountRequestStatus.ACCEPTEE
                && parsed != AccountRequestStatus.REFUSEE) {
            throw new BusinessRuleException(
                    "Décision invalide. Valeurs acceptées : ACCEPTEE, REFUSEE");
        }
        return parsed;
    }

    private RequestResult toResult(AccountRequest request, boolean emailSent) {
        return new RequestResult(
                request.getId(),
                request.getNom(),
                request.getPrenom(),
                request.getEmail(),
                request.getFormation(),
                request.getRequestedRole(),
                request.getCommentaire(),
                request.getStatut(),
                request.getDateCreation(),
                request.getDateTraitement(),
                emailSent);
    }
}
