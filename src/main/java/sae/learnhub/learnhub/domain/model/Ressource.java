package sae.learnhub.learnhub.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ressource {

    private Long id;
    private String nom;
    private String url;
    private String type;
    private Boolean telechargeable = true;
    private LocalDateTime dateCreation;
    private Chapitre chapitre;

    public void initialiserNouveau() {
        this.dateCreation = LocalDateTime.now();
    }
}
