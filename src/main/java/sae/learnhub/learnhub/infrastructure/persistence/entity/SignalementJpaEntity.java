package sae.learnhub.learnhub.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "signalement")
@Data
public class SignalementJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String sujet;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 40)
    private String categorie;

    @Column(nullable = false, length = 30)
    private String statut;

    @Column(name = "date_envoi", nullable = false, updatable = false)
    private LocalDateTime dateEnvoi;

    @Column(name = "piece_jointe_nom", length = 255)
    private String pieceJointeNom;

    @Column(name = "piece_jointe_url", length = 1000)
    private String pieceJointeUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auteur_id", nullable = false)
    private UserJpaEntity auteur;

    @PrePersist
    protected void onCreate() {
        if (dateEnvoi == null) {
            dateEnvoi = LocalDateTime.now();
        }
        if (statut == null || statut.isBlank()) {
            statut = "NOUVEAU";
        }
    }
}
