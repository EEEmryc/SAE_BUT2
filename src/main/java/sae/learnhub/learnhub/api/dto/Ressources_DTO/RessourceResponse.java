package sae.learnhub.learnhub.api.dto.Ressources_DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RessourceResponse {

    private Long id;
    private String nom;
    private String url;
    private String type;
    private Boolean telechargeable;
    private LocalDateTime dateCreation;
    private Long chapitreId;
    private String chapitreTitre;
}
