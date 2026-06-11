package sae.learnhub.learnhub.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Progression {

    private Long id;
    private String statut = "NON_COMMENCE";
    private Integer pourcentage = 0;
    private LocalDateTime dateDebut;
    private LocalDateTime dateMiseAJour;
    private LocalDateTime dateFin;
    private User eleve;
    private Cours cours;
    private Chapitre chapitre;
    private Ressource ressource;

    public void demarrer() {
        this.dateDebut = LocalDateTime.now();
        this.dateMiseAJour = LocalDateTime.now();
    }

    public void mettreAJour() {
        this.dateMiseAJour = LocalDateTime.now();
        if ("TERMINE".equals(this.statut) && this.dateFin == null) {
            this.dateFin = LocalDateTime.now();
        }
    }
}