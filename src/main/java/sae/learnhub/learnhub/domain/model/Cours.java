package sae.learnhub.learnhub.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cours {

    private Long id;

    private String titre;

    private String description;

    private LocalDateTime dateCreation;

    private String statut;

    private boolean visibleCatalogue;

    private String fichierPrincipalNom;

    private String fichierPrincipalUrl;

    private String fichierPrincipalType;

    private Long fichierPrincipalTailleOctets;

    private User prof;

    private List<Inscription> inscriptions = new ArrayList<>();

    private List<Chapitre> chapitres = new ArrayList<>();

    public void onCreate() {
        this.dateCreation = LocalDateTime.now();
        this.statut = CoursStatut.DRAFT.name();
        this.visibleCatalogue = true;
    }
}
