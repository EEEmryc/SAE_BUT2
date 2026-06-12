package sae.learnhub.learnhub.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inscription {

    private Long id;

    private LocalDateTime dateInscription;

    private String statut;

    private User eleve;

    private Cours cours;

    public void initialiserNouvelleInscription() {
        this.dateInscription = LocalDateTime.now();
        if (this.statut == null) {
            this.statut = InscriptionStatut.EN_ATTENTE.name();
        }
    }

    // Optionnel : Des méthodes purement métier pour faire évoluer le statut
    // Exemple :
    // public void valider() { this.statut = "VALIDEE"; }
    // public void rejeter() { this.statut = "REJETEE"; }
}