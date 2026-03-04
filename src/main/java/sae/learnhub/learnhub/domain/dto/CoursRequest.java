package sae.learnhub.learnhub.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoursRequest {
    
    @NotBlank
    @Size(max = 200)
    private String titre;
    
    @Size(max = 2000)
    private String description;
    
    private String statut;
    
    private Boolean visibleCatalogue;
}
