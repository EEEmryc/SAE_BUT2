package sae.learnhub.learnhub.api.dto.Messagerie_DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RepondreRequest {

    @NotBlank(message = "Le contenu de la réponse est obligatoire")
    private String contenu;
}
