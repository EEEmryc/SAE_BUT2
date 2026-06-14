package sae.learnhub.learnhub.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chapitre {

    private Long id;

    private String titre;

    private String contenu;

    private Integer ordre;

    private LocalDateTime dateCreation;

    private String fichierPrincipalNom;

    private String fichierPrincipalUrl;

    private String fichierPrincipalType;

    private Long fichierPrincipalTailleOctets;

    private Cours cours;
}
