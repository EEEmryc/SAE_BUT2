package sae.learnhub.learnhub.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressionResponse {

    private Long id;
    private String statut;
    private Integer pourcentage;
    private LocalDateTime dateDebut;
    private LocalDateTime dateMiseAJour;
    private LocalDateTime dateFin;

    // Context
    private Long eleveId;
    private String eleveNom;
    private String elevePrenom;

    private Long coursId;
    private String coursTitre;

    private Long chapitreId;
    private String chapitreTitre;

    private Long ressourceId;
    private String ressourceNom;
}
