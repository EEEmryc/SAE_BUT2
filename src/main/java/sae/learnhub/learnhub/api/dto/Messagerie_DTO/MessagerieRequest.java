package sae.learnhub.learnhub.api.dto.Messagerie_DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessagerieRequest {

    private String sujet;

    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;

    @NotNull(message = "L'email du destinataire est obligatoire")
    private String emailDestinataire;
}
