package sae.learnhub.learnhub.api.dto.messagerie;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RepondreRequest(
        @NotBlank(message = "Le contenu de la réponse est obligatoire")
        @Size(max = 2000, message = "La réponse ne doit pas dépasser 2000 caractères")
        String contenu
) {}
