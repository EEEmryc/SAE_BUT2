package sae.learnhub.learnhub.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageResponse {
    private Long id;
    private String contenu;
    private LocalDateTime dateEnvoi;
    private boolean lu;
    private Long expediteurId;
    private String expediteurNom;
    private Long destinataireId;
}