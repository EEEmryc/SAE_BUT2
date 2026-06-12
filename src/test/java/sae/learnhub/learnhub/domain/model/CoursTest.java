package sae.learnhub.learnhub.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoursTest {

    @Test
    void onCreate_initialise_statutDraftEtVisibleCatalogue() {
        Cours cours = new Cours();

        cours.onCreate();

        assertEquals("DRAFT", cours.getStatut());
        assertTrue(cours.isVisibleCatalogue());
        assertNotNull(cours.getDateCreation());
    }

    @Test
    void titreEtDescription_sontBienSettes() {
        Cours cours = new Cours();

        cours.setTitre("Introduction à Java");
        cours.setDescription("Cours pour débutants");

        assertEquals("Introduction à Java", cours.getTitre());
        assertEquals("Cours pour débutants", cours.getDescription());
    }

    @Test
    void statutsCours_DraftValideArchive_sontDesValeursValides() {
        Cours cours = new Cours();

        cours.setStatut("DRAFT");
        assertEquals("DRAFT", cours.getStatut());

        cours.setStatut("VALIDE");
        assertEquals("VALIDE", cours.getStatut());

        cours.setStatut("ARCHIVE");
        assertEquals("ARCHIVE", cours.getStatut());
    }
}
