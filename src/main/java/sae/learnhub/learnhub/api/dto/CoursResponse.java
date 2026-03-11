package sae.learnhub.learnhub.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoursResponse {

    private Long id;
    private String titre;
    private String description;
    private LocalDateTime dateCreation;
    private String statut;
    private Boolean visibleCatalogue;
    private String profNom;
    private String profPrenom;
    private String profEmail;
}
