package sae.learnhub.learnhub.api.dto.signalement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignalementRequest(
        @NotBlank(message = "Le sujet est obligatoire")
        @Size(max = 180, message = "Le sujet ne peut pas dépasser 180 caractères")
        String sujet,

        @NotBlank(message = "La description est obligatoire")
        @Size(max = 5000, message = "La description ne peut pas dépasser 5000 caractères")
        String description,

        @NotBlank(message = "La catégorie est obligatoire")
        String categorie,

        @Size(max = 255, message = "Le nom de la pièce jointe est trop long")
        String pieceJointeNom,

        @Size(max = 1000, message = "L'URL de la pièce jointe est trop longue")
        String pieceJointeUrl
) {
}
