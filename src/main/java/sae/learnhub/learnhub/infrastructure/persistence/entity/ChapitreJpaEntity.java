package sae.learnhub.learnhub.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "chapitre")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapitreJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "ordre")
    private Integer ordre;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "fichier_principal_nom", length = 255)
    private String fichierPrincipalNom;

    @Column(name = "fichier_principal_url", length = 500)
    private String fichierPrincipalUrl;

    @Column(name = "fichier_principal_type", length = 50)
    private String fichierPrincipalType;

    @Column(name = "fichier_principal_taille_octets")
    private Long fichierPrincipalTailleOctets;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cours_id", nullable = false)
    private CoursJpaEntity cours;

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }
}
