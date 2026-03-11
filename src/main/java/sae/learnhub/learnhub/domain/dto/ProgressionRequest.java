package sae.learnhub.learnhub.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProgressionRequest {

    @NotNull(message = "L'id du cours est obligatoire")
    private Long coursId;

    // null = course-level tracking
    private Long chapitreId;

    // null = chapter-level tracking
    private Long ressourceId;

    @NotNull(message = "Le statut est obligatoire")
    private String statut; // NON_COMMENCE | EN_COURS | TERMINE

    @NotNull
    @Min(0)
    @Max(100)
    private Integer pourcentage;
}
