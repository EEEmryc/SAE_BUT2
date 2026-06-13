package sae.learnhub.learnhub.application.Messagerie_Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sae.learnhub.learnhub.application.exception.AccessDeniedException;
import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.application.exception.ResourceNotFoundException;
import sae.learnhub.learnhub.domain.model.Messagerie;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.IMessagerieRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessagerieService {

    private final IMessagerieRepository messagerieRepository;
    private final IUserRepository userRepository;

    public record MessagerieCommand(String emailDestinataire, String sujet, String contenu) {}
    
    public record MessagerieResult(
            Long id, String sujet, String contenu, LocalDateTime dateEnvoi,
            Boolean lu, LocalDateTime dateLecture,
            Long expediteurId, String expediteurNom, String expediteurPrenom, String expediteurEmail,
            Long destinataireId, String destinataireNom, String destinatairePrenom, String destinataireEmail
    ) {}

    public record DestinataireResult(
            Long id, String nom, String prenom, String email, String role
    ) {}

    @Transactional
    public MessagerieResult envoyer(MessagerieCommand command, String expediteurEmail) {
        String expediteurNormalise = normalizeEmail(expediteurEmail);
        String destinataireNormalise = normalizeEmail(command.emailDestinataire());

        User expediteur = userRepository.findByEmail(expediteurNormalise)
                .orElseThrow(() -> new ResourceNotFoundException("Expéditeur introuvable"));

        User destinataire = userRepository.findByEmail(destinataireNormalise)
                .orElseThrow(() -> new ResourceNotFoundException("Destinataire introuvable : " + destinataireNormalise));

        if ("INACTIF".equalsIgnoreCase(destinataire.getStatut())) {
            throw new BusinessRuleException("Ce destinataire est inactif");
        }
        if (expediteur.getId().equals(destinataire.getId())) {
            throw new BusinessRuleException("Vous ne pouvez pas vous envoyer un message");
        }

        Messagerie msg = new Messagerie();
        msg.setSujet(command.sujet().trim());
        msg.setContenu(command.contenu().trim());
        msg.setExpediteur(expediteur);
        msg.setDestinataire(destinataire);
        msg.envoyer(); 

        return toResult(messagerieRepository.save(msg));
    }

    @Transactional
    public MessagerieResult repondre(Long messageId, String contenu, String expediteurEmail) {
        Messagerie original = messagerieRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message introuvable"));

        if (!original.getDestinataire().getEmail().equalsIgnoreCase(expediteurEmail)) {
            throw new AccessDeniedException("Seul le destinataire peut répondre à ce message");
        }

        MessagerieCommand reply = new MessagerieCommand(
                original.getExpediteur().getEmail(),
                buildReplySubject(original.getSujet()),
                contenu
        );

        return envoyer(reply, expediteurEmail);
    }

    public List<MessagerieResult> getInbox(String email) {
        return messagerieRepository.findByDestinataireEmailOrderByDateEnvoiDesc(normalizeEmail(email))
                .stream().map(this::toResult).toList();
    }

    public List<MessagerieResult> getOutbox(String email) {
        return messagerieRepository.findByExpediteurEmailOrderByDateEnvoiDesc(normalizeEmail(email))
                .stream().map(this::toResult).toList();
    }

    @Transactional
    public MessagerieResult getById(Long id, String email) {
        Messagerie msg = messagerieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message introuvable"));

        boolean isParticipant = msg.getExpediteur().getEmail().equalsIgnoreCase(email)
                || msg.getDestinataire().getEmail().equalsIgnoreCase(email);
        if (!isParticipant) {
            throw new AccessDeniedException("Accès refusé");
        }

        if (msg.getDestinataire().getEmail().equalsIgnoreCase(email) && !Boolean.TRUE.equals(msg.getLu())) {
            msg.marquerCommeLu();
            messagerieRepository.save(msg);
        }

        return toResult(msg);
    }

    public long countUnread(String email) {
        return messagerieRepository.countByDestinataireEmailAndLuFalse(normalizeEmail(email));
    }

    @Transactional
    public MessagerieResult markAsRead(Long id, String email) {
        Messagerie msg = messagerieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message introuvable"));

        if (!msg.getDestinataire().getEmail().equalsIgnoreCase(email)) {
            throw new AccessDeniedException("Seul le destinataire peut marquer ce message comme lu");
        }

        msg.marquerCommeLu();
        return toResult(messagerieRepository.save(msg));
    }

    public List<DestinataireResult> getDestinataires(String currentUserEmail) {
        return userRepository.findAll().stream()
                .filter(user -> !user.getEmail().equalsIgnoreCase(currentUserEmail))
                .filter(user -> !"INACTIF".equalsIgnoreCase(user.getStatut()))
                .sorted(Comparator.comparing(User::getNom, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(User::getPrenom, String.CASE_INSENSITIVE_ORDER))
                .map(user -> new DestinataireResult(
                        user.getId(),
                        user.getNom(),
                        user.getPrenom(),
                        user.getEmail(),
                        user.getRole()))
                .toList();
    }

    private MessagerieResult toResult(Messagerie m) {
        return new MessagerieResult(
                m.getId(), m.getSujet(), m.getContenu(), m.getDateEnvoi(),
                m.getLu(), m.getDateLecture(),
                m.getExpediteur().getId(), m.getExpediteur().getNom(), m.getExpediteur().getPrenom(), m.getExpediteur().getEmail(),
                m.getDestinataire().getId(), m.getDestinataire().getNom(), m.getDestinataire().getPrenom(), m.getDestinataire().getEmail()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String buildReplySubject(String subject) {
        String normalizedSubject = subject == null ? "" : subject.trim();
        return normalizedSubject.regionMatches(true, 0, "Re:", 0, 3)
                ? normalizedSubject
                : "Re: " + normalizedSubject;
    }
}
