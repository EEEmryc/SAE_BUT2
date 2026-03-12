package sae.learnhub.learnhub.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.LocalDateTime;

@Entity
@Table(name = "messagerie")
@Data
public class Messagerie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String sujet;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "date_envoi")
    private LocalDateTime dateEnvoi;

    @Column(nullable = false)
    private Boolean lu = false;

    @Column(name = "date_lecture")
    private LocalDateTime dateLecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediteur_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User expediteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User destinataire;

    @PrePersist
    protected void onCreate() {
        dateEnvoi = LocalDateTime.now();
    }
}
