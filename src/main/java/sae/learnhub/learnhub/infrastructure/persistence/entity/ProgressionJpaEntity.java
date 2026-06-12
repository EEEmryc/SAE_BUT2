package sae.learnhub.learnhub.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "progression", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "eleve_id", "cours_id", "chapitre_id", "ressource_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String statut = "NON_COMMENCE";

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
    private UserJpaEntity eleve;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cours_id", nullable = false)
    private CoursJpaEntity cours;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapitre_id")
    private ChapitreJpaEntity chapitre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ressource_id")
    private RessourceJpaEntity ressource;
}