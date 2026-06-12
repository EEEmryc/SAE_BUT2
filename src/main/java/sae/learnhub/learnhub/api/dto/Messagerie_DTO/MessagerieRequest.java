package sae.learnhub.learnhub.api.dto.Messagerie_DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MessagerieRequest(
        @NotBlank @Email String emailDestinataire,
        @NotBlank String sujet,
        @NotBlank String contenu
) {}