
package sae.learnhub.learnhub.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.ICoursRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;
import sae.learnhub.learnhub.domain.repository.IInscriptionRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ICoursRepository coursRepository;

    @Autowired
    private IInscriptionRepository inscriptionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long coursId;
    private Long eleveId;

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
        eleve.setRole("ETUDIANT");
        eleveId = userRepository.save(eleve).getId();

        User professeur = new User();
        professeur.setEmail("prof@test.com");
        professeur.setNom("Prof");
        professeur.setPrenom("Test");
        professeur.setPassword("password");
        professeur.setRole("PROFESSEUR");
        professeur = userRepository.save(professeur);

        Cours cours = new Cours();
        cours.setTitre("Cours Test");
        cours.setStatut("PUBLISHED");
        cours.setVisibleCatalogue(true);
        cours.setProf(professeur);
        coursId = coursRepository.save(cours).getId();
    }

    @Test
    @WithMockUser(username = "eleve@test.com", roles = "ETUDIANT")
    void shouldRegisterToCourse() throws Exception {
        mockMvc.perform(post("/api/inscriptions/cours/" + coursId))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "eleve@test.com", roles = "ETUDIANT")
    void shouldReturnBadRequestIfAlreadyRegistered() throws Exception {
        mockMvc.perform(post("/api/inscriptions/cours/" + coursId));

        mockMvc.perform(post("/api/inscriptions/cours/" + coursId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "eleve@test.com", roles = "ETUDIANT")
    void shouldReturnInscriptionsList() throws Exception {
        mockMvc.perform(get("/api/inscriptions/mes-inscriptions"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "prof@test.com", roles = "PROFESSEUR")
    void professeurPeutInscrirePuisRetirerUnEtudiant() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/inscriptions/cours/" + coursId + "/etudiants")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"eleveId\":" + eleveId + "}"))
                .andExpect(status().isCreated())
                .andReturn();
        Long inscriptionId = objectMapper.readTree(
                result.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/inscriptions/" + inscriptionId))
                .andExpect(status().isNoContent());
    }
}


