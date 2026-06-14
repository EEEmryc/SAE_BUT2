package sae.learnhub.learnhub.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sae.learnhub.learnhub.application.Auth_Service.AuthService;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.ISignalementRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SignalementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ISignalementRepository signalementRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        signalementRepository.deleteAll();
        userRepository.deleteAll();

        saveUser("Martin", "Sophie", "sophie@learnhub.fr", "ETUDIANT");
        saveUser("Dupont", "Jean", "jean@learnhub.fr", "PROFESSEUR");
        saveUser("AIT HAMI", "Yacine", "admin@learnhub.fr", "ADMIN");
    }

    @Test
    void studentCanCreateAndAdminCanProcessReport() throws Exception {
        String studentToken = token("sophie@learnhub.fr");
        String adminToken = token("admin@learnhub.fr");

        String response = mockMvc.perform(post("/api/signalements")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sujet": "Contenu inapproprié",
                                  "description": "Le chapitre contient un passage inadapté.",
                                  "categorie": "CONTENU",
                                  "pieceJointeNom": "capture.png",
                                  "pieceJointeUrl": "https://files.learnhub.local/capture.png"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statut").value("NOUVEAU"))
                .andExpect(jsonPath("$.auteurEmail").value("sophie@learnhub.fr"))
                .andExpect(jsonPath("$.auteurRole").value("ETUDIANT"))
                .andExpect(jsonPath("$.dateEnvoi").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long reportId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/signalements")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(reportId));

        mockMvc.perform(patch("/api/signalements/{id}/statut", reportId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statut\":\"EN_COURS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("EN_COURS"));
    }

    @Test
    void userCanListOnlyTheirOwnReports() throws Exception {
        String studentToken = token("sophie@learnhub.fr");
        String professorToken = token("jean@learnhub.fr");

        mockMvc.perform(post("/api/signalements")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sujet": "Probleme etudiant",
                                  "description": "Description du probleme etudiant.",
                                  "categorie": "TECHNIQUE"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/signalements")
                        .header("Authorization", "Bearer " + professorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sujet": "Probleme prof",
                                  "description": "Description du probleme professeur.",
                                  "categorie": "AUTRE"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/signalements/mes-signalements")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].sujet").value("Probleme etudiant"));
    }

    @Test
    void nonAdminCannotListOrChangeReports() throws Exception {
        String studentToken = token("sophie@learnhub.fr");

        mockMvc.perform(get("/api/signalements")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/signalements/1/statut")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statut\":\"RESOLU\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCannotCreateReportAndInvalidStatusIsRejected() throws Exception {
        String professorToken = token("jean@learnhub.fr");
        String adminToken = token("admin@learnhub.fr");

        String response = mockMvc.perform(post("/api/signalements")
                        .header("Authorization", "Bearer " + professorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sujet": "Erreur d'évaluation",
                                  "description": "La note affichée ne correspond pas au résultat.",
                                  "categorie": "EVALUATION"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode created = objectMapper.readTree(response);

        mockMvc.perform(patch("/api/signalements/{id}/statut", created.get("id").asLong())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statut\":\"FERME\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/signalements")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sujet": "Test",
                                  "description": "Un administrateur ne doit pas créer ce signalement.",
                                  "categorie": "AUTRE"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    private void saveUser(String nom, String prenom, String email, String role) {
        User user = new User();
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("pass123"));
        user.setRole(role);
        user.setStatut("ACTIF");
        userRepository.save(user);
    }

    private String token(String email) {
        return authService.login(new AuthService.LoginCommand(email, "pass123")).token();
    }
}