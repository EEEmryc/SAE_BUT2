package sae.learnhub.learnhub.application.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import sae.learnhub.learnhub.api.dto.MessagerieRequest;
import sae.learnhub.learnhub.api.dto.MessagerieResponse;
import sae.learnhub.learnhub.domain.model.Messagerie;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.MessagerieRepository;
import sae.learnhub.learnhub.domain.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessagerieService {

    private final MessagerieRepository messagerieRepository;
    private final UserRepository userRepository;

    public MessagerieResponse envoyer(MessagerieRequest request, String expediteurEmail) {
        User expediteur = userRepository.findByEmail(expediteurEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Expéditeur introuvable"));

        User destinataire = userRepository.findByEmail(request.getEmailDestinataire())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Destinataire introuvable : " + request.getEmailDestinataire()));

        Messagerie msg = new Messagerie();
        msg.setSujet(request.getSujet());
        msg.setContenu(request.getContenu());
        msg.setExpediteur(expediteur);
        msg.setDestinataire(destinataire);

        return toResponse(messagerieRepository.save(msg));
    }

    public MessagerieResponse repondre(Long messageId, String contenu, String expediteurEmail) {
        Messagerie original = messagerieRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message introuvable"));

        // Reply goes to the original sender
        MessagerieRequest reply = new MessagerieRequest();
        reply.setSujet("Re: " + (original.getSujet() != null ? original.getSujet() : ""));
        reply.setContenu(contenu);
        reply.setEmailDestinataire(original.getExpediteur().getEmail());

        return envoyer(reply, expediteurEmail);
    }

    public List<MessagerieResponse> getInbox(String email) {
        return messagerieRepository.findByDestinataireEmailOrderByDateEnvoiDesc(email)
                .stream().map(this::toResponse).toList();
    }

    public List<MessagerieResponse> getOutbox(String email) {
        return messagerieRepository.findByExpediteurEmailOrderByDateEnvoiDesc(email)
                .stream().map(this::toResponse).toList();
    }

    public MessagerieResponse getById(Long id, String email) {
        Messagerie msg = messagerieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message introuvable"));

        // Only sender or receiver can read it
        boolean isParticipant = msg.getExpediteur().getEmail().equals(email)
                || msg.getDestinataire().getEmail().equals(email);
        if (!isParticipant) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }

        // Mark as read if receiver opens it
        if (msg.getDestinataire().getEmail().equals(email) && !msg.getLu()) {
            msg.setLu(true);
            msg.setDateLecture(LocalDateTime.now());
            messagerieRepository.save(msg);
        }

        return toResponse(msg);
    }

    public long countUnread(String email) {
        return messagerieRepository.countByDestinataireEmailAndLuFalse(email);
    }

    private MessagerieResponse toResponse(Messagerie m) {
        return new MessagerieResponse(
                m.getId(),
                m.getSujet(),
                m.getContenu(),
                m.getDateEnvoi(),
                m.getLu(),
                m.getDateLecture(),
                m.getExpediteur().getId(),
                m.getExpediteur().getNom(),
                m.getExpediteur().getPrenom(),
                m.getExpediteur().getEmail(),
                m.getDestinataire().getId(),
                m.getDestinataire().getNom(),
                m.getDestinataire().getPrenom(),
                m.getDestinataire().getEmail());
    }
}
