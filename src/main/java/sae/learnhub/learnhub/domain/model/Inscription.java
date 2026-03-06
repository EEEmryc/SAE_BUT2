package sae.learnhub.learnhub.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscription")
@Data
@NoArgsConstructor
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_inscription")
    private LocalDateTime dateInscription;

    private String statut;

    @ManyToOne
    @JoinColumn(name = "eleve_id", nullable = false)
    private User eleve;

    @ManyToOne
    @JoinColumn(name = "cours_id", nullable = false)
    private Cours cours;

    @PrePersist
    protected void onCreate() {
        this.dateInscription = LocalDateTime.now();
        if (this.statut == null) {
            this.statut = "EN_ATTENTE";
        }
    }
}