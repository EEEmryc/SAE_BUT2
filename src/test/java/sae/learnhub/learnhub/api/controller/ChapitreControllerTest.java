package sae.learnhub.learnhub.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import sae.learnhub.learnhub.api.dto.Auth_DTO.LoginRequest;
import sae.learnhub.learnhub.application.Auth_Service.AuthService;
import sae.learnhub.learnhub.domain.model.Chapitre;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.ChapitreRepository;
import sae.learnhub.learnhub.domain.repository.CoursRepository;
import sae.learnhub.learnhub.domain.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ChapitreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChapitreRepository chapitreRepository;

    @Autowired
    private CoursRepository coursRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    private Long coursId;
    private Long chapitreId;

    @BeforeEach
    void setup() {
        chapitreRepository.deleteAll();
        coursRepository.deleteAll();
        userRepository.deleteAll();

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Créer un professeur
        User prof = new User();
        prof.setNom("Prof");
        prof.setPrenom("Test");
        prof.setEmail("prof@test.com");
        prof.setPassword(passwordEncoder.encode("password123"));
        prof.setRole("PROFESSEUR");
        prof.setStatut("ACTIF");
        userRepository.save(prof);

        // Créer un cours pour le prof
        Cours cours = new Cours();
        cours.setTitre("Cours Test");
        cours.setDescription("Description test");
        cours.setStatut("PUBLIE");
        cours.setVisibleCatalogue(true);
        cours.setProf(prof);
        Cours savedCours = coursRepository.save(cours);
        coursId = savedCours.getId();

        // Créer un chapitre pour le cours
        Chapitre chapitre = new Chapitre();
        chapitre.setTitre("Chapitre Test");
        chapitre.setContenu("Contenu du chapitre test");
        chapitre.setOrdre(1);
        chapitre.setCours(savedCours);
        Chapitre savedChapitre = chapitreRepository.save(chapitre);
        chapitreId = savedChapitre.getId();
    }

    @Test
    void testGetAllChapitresByCours() throws Exception {
        mockMvc.perform(get("/api/cours/{coursId}/chapitres", coursId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(chapitreId))
                .andExpect(jsonPath("$[0].titre").value("Chapitre Test"))
                .andExpect(jsonPath("$[0].contenu").value("Contenu du chapitre test"))
                .andExpect(jsonPath("$[0].ordre").value(1));
    }

    @Test
    void testCreateChapitre() throws Exception {
        String jwtToken = getJwtToken("prof@test.com", "password123");

        String newChapitreJson = """
                {
                    "titre": "Nouveau Chapitre",
                    "contenu": "Contenu du nouveau chapitre",
                    "ordre": 2
                }
                """;

        mockMvc.perform(post("/api/cours/{coursId}/chapitres", coursId)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newChapitreJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.titre").value("Nouveau Chapitre"))
                .andExpect(jsonPath("$.contenu").value("Contenu du nouveau chapitre"))
                .andExpect(jsonPath("$.ordre").value(2));
    }

    @Test
    void testCreateChapitreWithoutAuth() throws Exception {
        String newChapitreJson = """
                {
                    "titre": "Nouveau Chapitre",
                    "contenu": "Contenu du nouveau chapitre",
                    "ordre": 2
                }
                """;

        mockMvc.perform(post("/api/cours/{coursId}/chapitres", coursId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newChapitreJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateChapitreWithWrongRole() throws Exception {
        // Créer un élève
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User eleve = new User();
        eleve.setNom("Eleve");
        eleve.setPrenom("Test");
        eleve.setEmail("eleve@test.com");
        eleve.setPassword(passwordEncoder.encode("password123"));
        eleve.setRole("ETUDIANT");
        eleve.setStatut("ACTIF");
        userRepository.save(eleve);

        // Obtenir un token pour l'élève
        String eleveToken = getJwtToken("eleve@test.com", "password123");

        String newChapitreJson = """
                {
                    "titre": "Nouveau Chapitre",
                    "contenu": "Contenu du nouveau chapitre",
                    "ordre": 2
                }
                """;

        mockMvc.perform(post("/api/cours/{coursId}/chapitres", coursId)
                .header("Authorization", "Bearer " + eleveToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newChapitreJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateChapitre() throws Exception {
        String jwtToken = getJwtToken("prof@test.com", "password123");

        String updatedChapitreJson = """
                {
                    "titre": "Chapitre Modifié",
                    "contenu": "Contenu modifié",
                    "ordre": 1
                }
                """;

        mockMvc.perform(put("/api/cours/{coursId}/chapitres/{chapitreId}", coursId, chapitreId)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedChapitreJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.titre").value("Chapitre Modifié"))
                .andExpect(jsonPath("$.contenu").value("Contenu modifié"));
    }

    @Test
    void testUpdateChapitreUnauthorized() throws Exception {
        String updatedChapitreJson = """
                {
                    "titre": "Chapitre Modifié",
                    "contenu": "Contenu modifié",
                    "ordre": 1
                }
                """;

        mockMvc.perform(put("/api/cours/{coursId}/chapitres/{chapitreId}", coursId, chapitreId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedChapitreJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteChapitre() throws Exception {
        String jwtToken = getJwtToken("prof@test.com", "password123");

        mockMvc.perform(delete("/api/cours/{coursId}/chapitres/{chapitreId}", coursId, chapitreId)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteChapitreUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/cours/{coursId}/chapitres/{chapitreId}", coursId, chapitreId))
                .andExpect(status().isUnauthorized());
    }

    private String getJwtToken(String email, String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);
        return authService.login(loginRequest).getToken();
    }
}
