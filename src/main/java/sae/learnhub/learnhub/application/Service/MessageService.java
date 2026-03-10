package sae.learnhub.learnhub.application.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sae.learnhub.learnhub.domain.dto.MessageRequest;
import sae.learnhub.learnhub.domain.dto.MessageResponse;
import sae.learnhub.learnhub.domain.model.Message;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.MessageRepository;
import sae.learnhub.learnhub.domain.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageResponse envoyerMessage(Long expediteurId, MessageRequest request) {
        User expediteur = userRepository.findById(expediteurId)
                .orElseThrow(() -> new RuntimeException("Expéditeur introuvable"));
        User destinataire = userRepository.findById(request.getDestinataireId())
                .orElseThrow(() -> new RuntimeException("Destinataire introuvable"));

        Message message = new Message();
        message.setContenu(request.getContenu());
        message.setExpediteur(expediteur);
        message.setDestinataire(destinataire);
        message.setDateEnvoi(LocalDateTime.now());
        message.setLu(false);

        Message savedMessage = messageRepository.save(message);
        return mapToResponse(savedMessage);
    }

    public List<MessageResponse> getConversation(Long userId1, Long userId2) {
        return messageRepository.findByExpediteurIdAndDestinataireIdOrExpediteurIdAndDestinataireIdOrderByDateEnvoiAsc(
                userId1, userId2, userId2, userId1)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private MessageResponse mapToResponse(Message message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setContenu(message.getContenu());
        response.setDateEnvoi(message.getDateEnvoi());
        response.setLu(message.isLu());
        response.setExpediteurId(message.getExpediteur().getId());
        response.setExpediteurNom(message.getExpediteur().getPrenom() + " " + message.getExpediteur().getNom());
        response.setDestinataireId(message.getDestinataire().getId());
        return response;
    }
}