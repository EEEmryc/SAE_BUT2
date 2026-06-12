package sae.learnhub.learnhub.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProgressionTest {

    @Test
    void statuts_NonCommenceEnCoursTermine_sontDesValeursValides() {
        Progression progression = new Progression();

        // Valeur par défaut
        assertEquals("NON_COMMENCE", progression.getStatut());

        progression.setStatut("EN_COURS");
        assertEquals("EN_COURS", progression.getStatut());

        progression.setStatut("TERMINE");
        assertEquals("TERMINE", progression.getStatut());
    }

    @Test
    void eleveEtStatut_sontBienSettes() {
        Progression progression = new Progression();
        User eleve = new User();
        eleve.setEmail("eleve@example.com");

        progression.setEleve(eleve);
        progression.setStatut("EN_COURS");

        assertEquals("eleve@example.com", progression.getEleve().getEmail());
        assertEquals("EN_COURS", progression.getStatut());
    }
}
