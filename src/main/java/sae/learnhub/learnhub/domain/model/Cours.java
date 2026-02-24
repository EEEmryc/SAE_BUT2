package sae.learnhub.learnhub.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Cours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @Column(length = 2000)
    private String description;

    private LocalDateTime dateCreation;

    private String statut;

    private boolean visibleCatalogue;

    @ManyToOne
    @JoinColumn(name = "prof_id")
    private User prof;

    @PrePersist
    public void onCreate() {
        this.dateCreation = LocalDateTime.now();
        this.statut = "DRAFT";
        this.visibleCatalogue = true;
    }
}