package sae.learnhub.learnhub.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RessourceRequest {
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 200, message = "Le nom ne doit pas dépasser 200 caractères")
    private String nom;
    
    @Size(max = 500, message = "L'URL ne doit pas dépasser 500 caractères")
    private String url;
    
    @Size(max = 50, message = "Le type ne doit pas dépasser 50 caractères")
    private String type;
    
    private Boolean telechargeable = true;
}
