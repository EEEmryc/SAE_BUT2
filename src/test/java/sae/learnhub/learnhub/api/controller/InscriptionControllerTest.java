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
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.CoursRepository;
import sae.learnhub.learnhub.domain.repository.UserRepository;
import sae.learnhub.learnhub.domain.repository.InscriptionRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoursRepository coursRepository;

    @Autowired
    private InscriptionRepository inscriptionRepository;

    private Long coursId;

    @BeforeEach
    void setup() {
        inscriptionRepository.deleteAll();
        coursRepository.deleteAll();
        userRepository.deleteAll();

        User eleve = new User();
        eleve.setEmail("eleve@test.com");
        eleve.setNom("Test");
        eleve.setPrenom("Eleve");
        eleve.setPassword("password");
        eleve.setRole("ELEVE");
        userRepository.save(eleve);

        Cours cours = new Cours();
        cours.setTitre("Cours Test");
        coursId = coursRepository.save(cours).getId();
    }

    @Test
    @WithMockUser(username = "eleve@test.com", roles = "ELEVE")
    void shouldRegisterToCourse() throws Exception {
        mockMvc.perform(post("/api/inscriptions/cours/" + coursId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "eleve@test.com", roles = "ELEVE")
    void shouldReturnBadRequestIfAlreadyRegistered() throws Exception {
        mockMvc.perform(post("/api/inscriptions/cours/" + coursId));
        
        mockMvc.perform(post("/api/inscriptions/cours/" + coursId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "eleve@test.com", roles = "ELEVE")
    void shouldReturnInscriptionsList() throws Exception {
        mockMvc.perform(get("/api/inscriptions/mes-inscriptions"))
                .andExpect(status().isOk());
    }
}