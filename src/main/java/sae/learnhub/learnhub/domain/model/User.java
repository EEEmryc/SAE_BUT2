package sae.learnhub.learnhub.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "utilisateur")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "mot_de_passe", nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 20)
    private String role; 

    @Column(length = 50)
    private String statut;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();
}