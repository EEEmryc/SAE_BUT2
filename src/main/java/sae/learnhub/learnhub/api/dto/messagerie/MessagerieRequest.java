package sae.learnhub.learnhub.api.dto.messagerie;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessagerieRequest(
        @NotBlank(message = "Le destinataire est obligatoire")
        @Email(message = "L'adresse email du destinataire est invalide")
        String emailDestinataire,
        @NotBlank(message = "Le sujet est obligatoire")
        @Size(max = 255, message = "Le sujet ne doit pas dépasser 255 caractères")
        String sujet,
        @NotBlank(message = "Le contenu est obligatoire")
        @Size(max = 2000, message = "Le contenu ne doit pas dépasser 2000 caractères")
        String contenu
) {}
