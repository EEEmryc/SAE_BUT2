package sae.learnhub.learnhub.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cours")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoursJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titre;

    @Column(length = 2000)
    private String description;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(length = 50)
    private String statut;

    @Column(name = "visible_catalogue")
    private boolean visibleCatalogue;

    @Column(name = "fichier_principal_nom", length = 255)
    private String fichierPrincipalNom;

    @Column(name = "fichier_principal_url", length = 500)
    private String fichierPrincipalUrl;

    @Column(name = "fichier_principal_type", length = 50)
    private String fichierPrincipalType;

    @Column(name = "fichier_principal_taille_octets")
    private Long fichierPrincipalTailleOctets;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prof_id")
    private UserJpaEntity prof;

    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InscriptionJpaEntity> inscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChapitreJpaEntity> chapitres = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }
}
