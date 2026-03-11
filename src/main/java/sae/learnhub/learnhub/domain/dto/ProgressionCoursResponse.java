package sae.learnhub.learnhub.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressionCoursResponse {

    private Long coursId;
    private String coursTitre;
    private int totalChapitres;
    private int chapitresTermines;
    private int pourcentageGlobal; // computed: chapitresTermines / totalChapitres * 100

    private List<ProgressionResponse> detailParChapitre;
}
