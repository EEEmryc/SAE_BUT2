package sae.learnhub.learnhub.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String formation;
    private String requestedRole;
    private String commentaire;
    private String statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateTraitement;

    public void soumettre() {
        statut = AccountRequestStatus.EN_ATTENTE.name();
        dateCreation = LocalDateTime.now();
        dateTraitement = null;
    }

    public void accepter() {
        statut = AccountRequestStatus.ACCEPTEE.name();
        dateTraitement = LocalDateTime.now();
    }

    public void refuser() {
        statut = AccountRequestStatus.REFUSEE.name();
        dateTraitement = LocalDateTime.now();
    }
}
