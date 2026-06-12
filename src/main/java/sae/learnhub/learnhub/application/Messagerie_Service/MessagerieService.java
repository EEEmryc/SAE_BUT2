package sae.learnhub.learnhub.application.Messagerie_Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import sae.learnhub.learnhub.domain.model.Messagerie;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.IMessagerieRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import java.time.LocalDateTime;
import java.util.List;

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

    @Transactional
    public MessagerieResult envoyer(MessagerieCommand command, String expediteurEmail) {
        User expediteur = userRepository.findByEmail(expediteurEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expéditeur introuvable"));

        User destinataire = userRepository.findByEmail(command.emailDestinataire())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Destinataire introuvable : " + command.emailDestinataire()));

        Messagerie msg = new Messagerie();
        msg.setSujet(command.sujet());
        msg.setContenu(command.contenu());
        msg.setExpediteur(expediteur);
        msg.setDestinataire(destinataire);
        msg.envoyer(); 

        return toResult(messagerieRepository.save(msg));
    }

    @Transactional
    public MessagerieResult repondre(Long messageId, String contenu, String expediteurEmail) {
        Messagerie original = messagerieRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message introuvable"));

        MessagerieCommand reply = new MessagerieCommand(
                original.getExpediteur().getEmail(),
                "Re: " + (original.getSujet() != null ? original.getSujet() : ""),
                contenu
        );

        return envoyer(reply, expediteurEmail);
    }

    public List<MessagerieResult> getInbox(String email) {
        return messagerieRepository.findByDestinataireEmailOrderByDateEnvoiDesc(email)
                .stream().map(this::toResult).toList();
    }

    public List<MessagerieResult> getOutbox(String email) {
        return messagerieRepository.findByExpediteurEmailOrderByDateEnvoiDesc(email)
                .stream().map(this::toResult).toList();
    }

    @Transactional
    public MessagerieResult getById(Long id, String email) {
        Messagerie msg = messagerieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message introuvable"));

        boolean isParticipant = msg.getExpediteur().getEmail().equals(email)
                || msg.getDestinataire().getEmail().equals(email);
        if (!isParticipant) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }

        if (msg.getDestinataire().getEmail().equals(email) && !msg.getLu()) {
            msg.setLu(true);
            msg.setDateLecture(LocalDateTime.now());
            messagerieRepository.save(msg);
        }

        return toResult(msg);
    }

    public long countUnread(String email) {
        return messagerieRepository.countByDestinataireEmailAndLuFalse(email);
    }

    private MessagerieResult toResult(Messagerie m) {
        return new MessagerieResult(
                m.getId(), m.getSujet(), m.getContenu(), m.getDateEnvoi(),
                m.getLu(), m.getDateLecture(),
                m.getExpediteur().getId(), m.getExpediteur().getNom(), m.getExpediteur().getPrenom(), m.getExpediteur().getEmail(),
                m.getDestinataire().getId(), m.getDestinataire().getNom(), m.getDestinataire().getPrenom(), m.getDestinataire().getEmail()
        );
    }
}