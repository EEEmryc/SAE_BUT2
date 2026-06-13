package sae.learnhub.learnhub.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Signalement {

    private Long id;
    private String sujet;
    private String description;
    private String categorie;
    private String statut;
    private LocalDateTime dateEnvoi;
    private String pieceJointeNom;
    private String pieceJointeUrl;
    private User auteur;

    public void soumettre() {
        this.statut = SignalementStatut.NOUVEAU.name();
        this.dateEnvoi = LocalDateTime.now();
    }

    public void changerStatut(SignalementStatut nouveauStatut) {
        this.statut = nouveauStatut.name();
    }
}
