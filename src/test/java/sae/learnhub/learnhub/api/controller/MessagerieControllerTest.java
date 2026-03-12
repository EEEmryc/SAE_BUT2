package sae.learnhub.learnhub.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import sae.learnhub.learnhub.api.dto.Auth_DTO.LoginRequest;
import sae.learnhub.learnhub.application.Auth_Service.AuthService;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.MessagerieRepository;
import sae.learnhub.learnhub.domain.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MessagerieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessagerieRepository messagerieRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long eleveId;
    private Long profId;

    @BeforeEach
    void setup() {
        // Nettoyage de la base avant chaque test
        messagerieRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Création de un eleve
        User eleve = new User();
        eleve.setNom("Toto");
        eleve.setPrenom("Bob");
        eleve.setEmail("bob@eleve.com");
        eleve.setPassword(passwordEncoder.encode("pass123"));
        eleve.setRole("ELEVE");
        eleve.setStatut("ACTIF");
        eleveId = userRepository.save(eleve).getId();

        // 2. Création d'un professeur
        User prof = new User();
        prof.setNom("Tata");
        prof.setPrenom("Alice");
        prof.setEmail("alice@prof.com");
        prof.setPassword(passwordEncoder.encode("pass123"));
        prof.setRole("PROFESSEUR");
        prof.setStatut("ACTIF");
        profId = userRepository.save(prof).getId();
    }

    private String getJwtToken(String email, String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);
        return authService.login(loginRequest).getToken();
    }

    @Test
    void testScenarioMessagerieEtudiantProfesseur() throws Exception {
        // Récupération des tokens pour simuler les connexions
        String tokenEleve = getJwtToken("bob@eleve.com", "pass123");
        String tokenProf = getJwtToken("alice@prof.com", "pass123");

        // TEST : L'élève envoie un message au professeur
        String messageEleveJson = """
                {
                    "emailDestinataire": "alice@prof.com",
                    "contenu": "Bonjour Madame, je n'ai pas compris le chapitre sur les API REST."
                }
                """;

        mockMvc.perform(post("/api/messages")
                .header("Authorization", "Bearer " + tokenEleve)
                .contentType(MediaType.APPLICATION_JSON)
                .content(messageEleveJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenu")
                        .value("Bonjour Madame, je n'ai pas compris le chapitre sur les API REST."))
                .andExpect(jsonPath("$.expediteurId").value(eleveId));

        // TEST : Le professeur répond à l'élève
        String messageProfJson = """
                {
                    "emailDestinataire": "bob@eleve.com",
                    "contenu": "Bonjour Bob, relis la documentation Swagger, tout y est !"
                }
                """;

        mockMvc.perform(post("/api/messages")
                .header("Authorization", "Bearer " + tokenProf)
                .contentType(MediaType.APPLICATION_JSON)
                .content(messageProfJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenu").value("Bonjour Bob, relis la documentation Swagger, tout y est !"))
                .andExpect(jsonPath("$.expediteurId").value(profId));

        // TEST : L'élève consulte sa boîte de réception (contient la réponse du prof)
        mockMvc.perform(get("/api/messages/recus")
                .header("Authorization", "Bearer " + tokenEleve))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].contenu").value("Bonjour Bob, relis la documentation Swagger, tout y est !"));
    }

    @Test
    void testAccesNonAutorise() throws Exception {
        // Tenter d'envoyer un message sans token JWT pour vérifier la sécurité
        String messageJson = """
                {
                    "emailDestinataire": "alice@prof.com",
                    "contenu": "Message pirate"
                }
                """;

        mockMvc.perform(post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(messageJson))
                .andExpect(status().isForbidden());
    }
}