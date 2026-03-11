package sae.learnhub.learnhub.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessagerieResponse {

    private Long id;
    private String sujet;
    private String contenu;
    private LocalDateTime dateEnvoi;
    private Boolean lu;
    private LocalDateTime dateLecture;

    // Sender info
    private Long expediteurId;
    private String expediteurNom;
    private String expediteurPrenom;
    private String expediteurEmail;

    // Receiver info
    private Long destinataireId;
    private String destinataireNom;
    private String destinatairePrenom;
    private String destinataireEmail;
}
