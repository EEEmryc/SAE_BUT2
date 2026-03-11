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
import sae.learnhub.learnhub.application.Service.AuthService;
import sae.learnhub.learnhub.domain.dto.LoginRequest;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.MessageRepository;
import sae.learnhub.learnhub.domain.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long eleveId;
    private Long profId;

    @BeforeEach
    void setup() {
        // Nettoyage de la base avant chaque test
        messageRepository.deleteAll();
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
                "destinataireId": %d,
                "contenu": "Bonjour Madame, je n'ai pas compris le chapitre sur les API REST."
            }
            """.formatted(profId);

        mockMvc.perform(post("/api/messages")
                .header("Authorization", "Bearer " + tokenEleve)
                .contentType(MediaType.APPLICATION_JSON)
                .content(messageEleveJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenu").value("Bonjour Madame, je n'ai pas compris le chapitre sur les API REST."))
                .andExpect(jsonPath("$.expediteurId").value(eleveId));

        // TEST : Le professeur répond à l'élève
        String messageProfJson = """
            {
                "destinataireId": %d,
                "contenu": "Bonjour Bob, relis la documentation Swagger, tout y est !"
            }
            """.formatted(eleveId);

        mockMvc.perform(post("/api/messages")
                .header("Authorization", "Bearer " + tokenProf)
                .contentType(MediaType.APPLICATION_JSON)
                .content(messageProfJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenu").value("Bonjour Bob, relis la documentation Swagger, tout y est !"))
                .andExpect(jsonPath("$.expediteurId").value(profId));

        // TEST : Le professeur consulte la conversation complète
        mockMvc.perform(get("/api/messages/conversation/" + eleveId)
                .header("Authorization", "Bearer " + tokenProf))
                .andExpect(status().isOk())
               
                .andExpect(jsonPath("$.length()").value(2))  //2 messages dans l'historique
                
                .andExpect(jsonPath("$[0].contenu").value("Bonjour Madame, je n'ai pas compris le chapitre sur les API REST."))// On vérifie l'ordre chronologique
                .andExpect(jsonPath("$[1].contenu").value("Bonjour Bob, relis la documentation Swagger, tout y est !"));
    }

    @Test
    void testAccesNonAutorise() throws Exception {
        // Tenter d'envoyer un message sans token JWT pour vérifier la sécurité
        String messageJson = """
            {
                "destinataireId": %d,
                "contenu": "Message pirate"
            }
            """.formatted(profId);

        mockMvc.perform(post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(messageJson))
                .andExpect(status().isForbidden());
    }
}