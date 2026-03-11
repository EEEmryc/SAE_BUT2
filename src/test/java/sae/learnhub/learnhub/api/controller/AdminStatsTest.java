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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminStatsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoursRepository coursRepository;

    @BeforeEach
    void setup() {
        coursRepository.deleteAll();
        userRepository.deleteAll();

        // Création d'un utilisateur
        User user = new User();
        user.setNom("Admin");
        user.setPrenom("User");
        user.setEmail("admin@test.com");
        user.setPassword("pass");
        user.setRole("ADMINISTRATEUR");
        userRepository.save(user);

        // Création d'un cours publié
        Cours cours = new Cours();
        cours.setTitre("Cours Actif");
        cours.setStatut("PUBLISHED");
        coursRepository.save(cours);
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATEUR")
    void shouldReturnCorrectStatisticsForAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUtilisateurs").value(1))
                .andExpect(jsonPath("$.totalCoursActifs").value(1));
    }

    @Test
    @WithMockUser(roles = "ELEVE")
    void shouldDenyAccessForNonAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isForbidden());
    }
}