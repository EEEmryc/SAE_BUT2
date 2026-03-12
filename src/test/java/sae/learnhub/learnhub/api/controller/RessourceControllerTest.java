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
import sae.learnhub.learnhub.domain.model.Ressource;
import sae.learnhub.learnhub.domain.model.Chapitre;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.RessourceRepository;
import sae.learnhub.learnhub.domain.repository.ChapitreRepository;
import sae.learnhub.learnhub.domain.repository.CoursRepository;
import sae.learnhub.learnhub.domain.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RessourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RessourceRepository ressourceRepository;

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
    private Long ressourceId;

    @BeforeEach
    void setup() {
        ressourceRepository.deleteAll();
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
        prof.setRole("ROLE_PROF");
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

        // Créer une ressource pour le chapitre
        Ressource ressource = new Ressource();
        ressource.setNom("Ressource Test");
        ressource.setUrl("https://example.com/resource.pdf");
        ressource.setType("PDF");
        ressource.setTelechargeable(true);
        ressource.setChapitre(savedChapitre);
        Ressource savedRessource = ressourceRepository.save(ressource);
        ressourceId = savedRessource.getId();
    }

    @Test
    void testGetAllRessourcesByChapitre() throws Exception {
        mockMvc.perform(get("/api/cours/{coursId}/chapitres/{chapitreId}/ressources", coursId, chapitreId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(ressourceId))
                .andExpect(jsonPath("$[0].nom").value("Ressource Test"))
                .andExpect(jsonPath("$[0].url").value("https://example.com/resource.pdf"))
                .andExpect(jsonPath("$[0].type").value("PDF"))
                .andExpect(jsonPath("$[0].telechargeable").value(true));
    }

    @Test
    void testCreateRessource() throws Exception {
        String jwtToken = getJwtToken("prof@test.com", "password123");

        String newRessourceJson = """
                {
                    "nom": "Nouvelle Ressource",
                    "url": "https://example.com/new-resource.pdf",
                    "type": "PDF",
                    "telechargeable": true
                }
                """;

        mockMvc.perform(post("/api/cours/{coursId}/chapitres/{chapitreId}/ressources", coursId, chapitreId)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newRessourceJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nom").value("Nouvelle Ressource"))
                .andExpect(jsonPath("$.url").value("https://example.com/new-resource.pdf"))
                .andExpect(jsonPath("$.type").value("PDF"))
                .andExpect(jsonPath("$.telechargeable").value(true));
    }

    @Test
    void testCreateRessourceWithoutAuth() throws Exception {
        String newRessourceJson = """
                {
                    "nom": "Nouvelle Ressource",
                    "url": "https://example.com/new-resource.pdf",
                    "type": "PDF",
                    "telechargeable": true
                }
                """;

        mockMvc.perform(post("/api/cours/{coursId}/chapitres/{chapitreId}/ressources", coursId, chapitreId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newRessourceJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateRessourceWithWrongRole() throws Exception {
        // Créer un élève
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User eleve = new User();
        eleve.setNom("Eleve");
        eleve.setPrenom("Test");
        eleve.setEmail("eleve@test.com");
        eleve.setPassword(passwordEncoder.encode("password123"));
        eleve.setRole("ROLE_ETUDIANT");
        eleve.setStatut("ACTIF");
        userRepository.save(eleve);

        // Obtenir un token pour l'élève
        String eleveToken = getJwtToken("eleve@test.com", "password123");

        String newRessourceJson = """
                {
                    "nom": "Nouvelle Ressource",
                    "url": "https://example.com/new-resource.pdf",
                    "type": "PDF",
                    "telechargeable": true
                }
                """;

        mockMvc.perform(post("/api/cours/{coursId}/chapitres/{chapitreId}/ressources", coursId, chapitreId)
                .header("Authorization", "Bearer " + eleveToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newRessourceJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateRessource() throws Exception {
        String jwtToken = getJwtToken("prof@test.com", "password123");

        String updatedRessourceJson = """
                {
                    "nom": "Ressource Modifiée",
                    "url": "https://example.com/updated-resource.pdf",
                    "type": "VIDEO",
                    "telechargeable": false
                }
                """;

        mockMvc.perform(put("/api/cours/{coursId}/chapitres/{chapitreId}/ressources/{ressourceId}", coursId, chapitreId,
                ressourceId)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedRessourceJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nom").value("Ressource Modifiée"))
                .andExpect(jsonPath("$.url").value("https://example.com/updated-resource.pdf"))
                .andExpect(jsonPath("$.type").value("VIDEO"))
                .andExpect(jsonPath("$.telechargeable").value(false));
    }

    @Test
    void testUpdateRessourceUnauthorized() throws Exception {
        String updatedRessourceJson = """
                {
                    "nom": "Ressource Modifiée",
                    "url": "https://example.com/updated-resource.pdf",
                    "type": "VIDEO",
                    "telechargeable": false
                }
                """;

        mockMvc.perform(put("/api/cours/{coursId}/chapitres/{chapitreId}/ressources/{ressourceId}", coursId, chapitreId,
                ressourceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedRessourceJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteRessource() throws Exception {
        String jwtToken = getJwtToken("prof@test.com", "password123");

        mockMvc.perform(delete("/api/cours/{coursId}/chapitres/{chapitreId}/ressources/{ressourceId}", coursId,
                chapitreId, ressourceId)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteRessourceUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/cours/{coursId}/chapitres/{chapitreId}/ressources/{ressourceId}", coursId,
                chapitreId, ressourceId))
                .andExpect(status().isForbidden());
    }

    private String getJwtToken(String email, String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);
        return authService.login(loginRequest).getToken();
    }
}
