package sae.learnhub.learnhub.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "progression", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "eleve_id", "cours_id", "chapitre_id", "ressource_id" })
})
@Data
public class Progression {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NON_COMMENCE | EN_COURS | TERMINE
    @Column(length = 50, nullable = false)
    private String statut = "NON_COMMENCE";

    // 0–100
    @Column(nullable = false)
    private Integer pourcentage = 0;

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_mise_a_jour")
    private LocalDateTime dateMiseAJour;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eleve_id", nullable = false)
    private User eleve;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cours_id", nullable = false)
    private Cours cours;

    // null = course-level tracking
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapitre_id")
    private Chapitre chapitre;

    // null = chapter-level tracking
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ressource_id")
    private Ressource ressource;

    @PrePersist
    protected void onCreate() {
        dateDebut = LocalDateTime.now();
        dateMiseAJour = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateMiseAJour = LocalDateTime.now();
        if ("TERMINE".equals(statut) && dateFin == null) {
            dateFin = LocalDateTime.now();
        }
    }
}
