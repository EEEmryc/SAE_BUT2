package sae.learnhub.learnhub.domain.dto;

import lombok.Data;

@Data
public class StatutRequest {
    /** Nouveau statut de l'inscription. Valeurs acceptées : VALIDE, REFUSE */
    private String statut;
}
