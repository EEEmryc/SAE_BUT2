package sae.learnhub.learnhub.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Locale;

@Entity
@Table(name = "demande_compte")
@Data
public class AccountRequestJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 180)
    private String formation;

    @Column(name = "role_demande", nullable = false, length = 20)
    private String requestedRole;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String commentaire;

    @Column(nullable = false, length = 30)
    private String statut;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
        if (statut == null || statut.isBlank()) {
            statut = "EN_ATTENTE";
        }
        email = email.trim().toLowerCase(Locale.ROOT);
        requestedRole = requestedRole.trim().toUpperCase(Locale.ROOT);
        statut = statut.trim().toUpperCase(Locale.ROOT);
    }
}
