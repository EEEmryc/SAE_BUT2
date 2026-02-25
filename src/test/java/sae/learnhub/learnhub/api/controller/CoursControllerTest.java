package sae.learnhub.learnhub.api.controller;

import org.junit.jupiter.api.BeforeEach;
import sae.learnhub.learnhub.domain.repository.UserRepository;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.model.Cours;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sae.learnhub.learnhub.domain.repository.CoursRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")

class CoursControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired UserRepository userRepository;
    @Autowired CoursRepository coursRepository;

    private Long coursId;

    @BeforeEach
    void setup() {
        coursRepository.deleteAll();
        userRepository.deleteAll();

        
        User prof1 = new User();
        prof1.setUsername("prof1");
        prof1.setPassword("x");
        prof1.setRole("PROF");
        userRepository.save(prof1);

        
        User prof2 = new User();
        prof2.setUsername("prof2");
        prof2.setPassword("x");
        prof2.setRole("PROF");
        userRepository.save(prof2);

        
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("x");
        admin.setRole("ADMIN");
        userRepository.save(admin);

        
        Cours cours = new Cours();
        cours.setTitre("Java");
        cours.setDescription("Base");
        cours.setStatut("DRAFT");
        cours.setVisibleCatalogue(true);
        cours.setProf(prof1);

        coursId = coursRepository.save(cours).getId();
    }

    
    @Test
    void getAllCours() throws Exception {
        mockMvc.perform(get("/api/cours"))
                .andExpect(status().isOk());
    }

    
    @Test
    @WithMockUser(username = "prof1", authorities = {"PROF"})
    void profUpdateCours() throws Exception {
        String body = """
            {
              "titre": "javaScript",
              "description": "lorem ipsum dolor sit amet consectetur adipiscing elit",
              "statut": "PUBLISHED",
              "visibleCatalogue": true
            }
        """;

        mockMvc.perform(put("/api/cours/" + coursId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    
    @Test
    @WithMockUser(username = "prof2", authorities = {"PROF"})
    void autreProfUpdateCours() throws Exception {
        String body = """
            {
              "titre": "Mathematique",
              "description": "lorem ipsum dolor sit amet consectetur adipiscing elit",
              "statut": "PUBLISHED",
              "visibleCatalogue": true
            }
        """;

        mockMvc.perform(put("/api/cours/" + coursId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void adminUpdateCours() throws Exception {
        String body = """
            {
              "titre": "Admin test",
              "description": "lorem ipsum dolor sit amet consectetur adipiscing elit",
              "statut": "PUBLISHED",
              "visibleCatalogue": true
            }
        """;

        mockMvc.perform(put("/api/cours/" + coursId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }
}