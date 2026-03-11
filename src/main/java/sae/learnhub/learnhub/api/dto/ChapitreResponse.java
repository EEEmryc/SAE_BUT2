package sae.learnhub.learnhub.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapitreResponse {

    private Long id;
    private String titre;
    private String contenu;
    private Integer ordre;
    private LocalDateTime dateCreation;
    private Long coursId;
    private String coursTitre;
}
