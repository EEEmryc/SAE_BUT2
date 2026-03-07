package sae.learnhub.learnhub.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chapitre")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chapitre {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String titre;
    
    @Column(columnDefinition = "TEXT")
    private String contenu;
    
    @Column(name = "ordre")
    private Integer ordre;
    
    @CreationTimestamp
    @Column(name = "date_creation")
    private LocalDateTime dateCreation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cours_id", nullable = false)
    private Cours cours;
}
