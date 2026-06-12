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
    private String statut = ProgressionStatut.NON_COMMENCE.name();
    private Integer pourcentage = 0;
    private LocalDateTime dateDebut;
    private LocalDateTime dateMiseAJour;
    private LocalDateTime dateFin;
    private User eleve;
    private Cours cours;
    private Chapitre chapitre;
    private Ressource ressource;

    public void demarrer() {
        LocalDateTime maintenant = LocalDateTime.now();
        this.statut = ProgressionStatut.EN_COURS.name();
        this.pourcentage = 0;
        this.dateDebut = maintenant;
        this.dateMiseAJour = maintenant;
        this.dateFin = null;
    }

    public void terminer() {
        LocalDateTime maintenant = LocalDateTime.now();
        this.statut = ProgressionStatut.TERMINE.name();
        this.pourcentage = 100;
        this.dateMiseAJour = maintenant;
        this.dateFin = maintenant;

        if (this.dateDebut == null) {
            this.dateDebut = maintenant;
        }
    }

    public void mettreAJour() {
        this.dateMiseAJour = LocalDateTime.now();
        if (ProgressionStatut.TERMINE.name().equals(this.statut) && this.dateFin == null) {
            this.dateFin = LocalDateTime.now();
        }
    }
}
