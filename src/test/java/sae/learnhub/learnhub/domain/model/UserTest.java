package sae.learnhub.learnhub.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    @Test
    void nomPrenomEmail_sontBienSettes() {
        User user = new User();

        user.setNom("Doe");
        user.setPrenom("John");
        user.setEmail("john.doe@example.com");

        assertEquals("Doe", user.getNom());
        assertEquals("John", user.getPrenom());
        assertEquals("john.doe@example.com", user.getEmail());
    }

    @Test
    void role_estBienAssigne() {
        User user = new User();

        user.setRole("PROFESSEUR");

        assertEquals("PROFESSEUR", user.getRole());
    }
}
