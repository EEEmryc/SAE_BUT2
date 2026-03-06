package sae.learnhub.learnhub.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.model.Inscription;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.CoursRepository;
import sae.learnhub.learnhub.domain.repository.InscriptionRepository;
import sae.learnhub.learnhub.domain.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CoursConsultationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private CoursRepository coursRepository;
    @Autowired private InscriptionRepository inscriptionRepository;

    @BeforeEach
    void setup() {
        inscriptionRepository.deleteAll();
        coursRepository.deleteAll();
        userRepository.deleteAll();

        User eleve = new User();
        eleve.setEmail("student@test.com");
        eleve.setNom("Student");
        eleve.setPrenom("Test");
        eleve.setPassword("pass");
        eleve.setRole("ELEVE");
        userRepository.save(eleve);

        // Cours 1 : Validé
        Cours c1 = new Cours(); c1.setTitre("Java Avancé");
        c1 = coursRepository.save(c1);
        Inscription i1 = new Inscription(); i1.setCours(c1); i1.setEleve(eleve); i1.setStatut("VALIDE");
        inscriptionRepository.save(i1);

        // Cours 2 : En attente
        Cours c2 = new Cours(); c2.setTitre("Python Bases");
        c2 = coursRepository.save(c2);
        Inscription i2 = new Inscription(); i2.setCours(c2); i2.setEleve(eleve); i2.setStatut("EN_ATTENTE");
        inscriptionRepository.save(i2);
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "ELEVE")
    void shouldOnlyReturnValidatedCourses() throws Exception {
        mockMvc.perform(get("/api/inscriptions/mes-cours-valides"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].cours.titre").value("Java Avancé"));
    }
}