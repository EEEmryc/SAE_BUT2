package sae.learnhub.learnhub.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "ressource")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ressource {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String nom;
    
    @Column(length = 500)
    private String url;
    
    @Column(length = 50)
    private String type;
    
    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean telechargeable = true;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapitre_id", nullable = false)
    private Chapitre chapitre;
    
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}
