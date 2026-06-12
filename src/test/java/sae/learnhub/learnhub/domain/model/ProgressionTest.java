package sae.learnhub.learnhub.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProgressionTest {

    @Test
    void demarrer_positionneLaProgressionEnCours() {
        Progression progression = new Progression();

        progression.demarrer();

        assertEquals("EN_COURS", progression.getStatut());
        assertEquals(0, progression.getPourcentage());
        assertNotNull(progression.getDateDebut());
        assertEquals(progression.getDateDebut(), progression.getDateMiseAJour());
        assertNull(progression.getDateFin());
    }

    @Test
    void terminer_positionneLaProgressionAcentPourcentEtRenseigneLesDates() {
        Progression progression = new Progression();
        progression.demarrer();

        progression.terminer();

        assertEquals("TERMINE", progression.getStatut());
        assertEquals(100, progression.getPourcentage());
        assertNotNull(progression.getDateDebut());
        assertNotNull(progression.getDateMiseAJour());
        assertEquals(progression.getDateMiseAJour(), progression.getDateFin());
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
