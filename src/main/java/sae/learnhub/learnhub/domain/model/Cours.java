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

    private User prof;

    private List<Inscription> inscriptions = new ArrayList<>();

    private List<Chapitre> chapitres = new ArrayList<>();

    public void initialiserNouveauCours() {
        this.dateCreation = LocalDateTime.now();
        this.statut = "DRAFT";
        this.visibleCatalogue = true;
    }

    // On pourra ajouter d'autres méthodes comme : public void publier() {
    // this.statut = "PUBLISHED"; }
}