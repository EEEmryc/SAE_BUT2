package sae.learnhub.learnhub.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Messagerie {

    private Long id;
    private String sujet;
    private String contenu;
    private LocalDateTime dateEnvoi;
    private Boolean lu = false;
    private LocalDateTime dateLecture;
    private User expediteur;
    private User destinataire;

    public void envoyer() {
        this.dateEnvoi = LocalDateTime.now();
        this.lu = false;
        this.dateLecture = null;
    }

    public void marquerCommeLu() {
        if (!Boolean.TRUE.equals(this.lu)) {
            this.lu = true;
            this.dateLecture = LocalDateTime.now();
        }
    }
}
