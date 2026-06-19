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

import sae.learnhub.learnhub.application.auth.AuthService;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.IInscriptionRepository;
import sae.learnhub.learnhub.domain.repository.IMessagerieRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MessagerieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IMessagerieRepository messagerieRepository;

    @Autowired
    private IInscriptionRepository inscriptionRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private Long eleveId;
    private Long profId;
    private Long outsiderId;

    @BeforeEach
    void setup() {
        // Nettoyage de la base avant chaque test
        messagerieRepository.deleteAll();
        inscriptionRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Création de un eleve
        User eleve = new User();
        eleve.setNom("Toto");
        eleve.setPrenom("Bob");
        eleve.setEmail("bob@eleve.com");
        eleve.setPassword(passwordEncoder.encode("pass123"));
        eleve.setRole("ETUDIANT");
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

        User outsider = new User();
        outsider.setNom("Martin");
        outsider.setPrenom("Chloé");
        outsider.setEmail("chloe@admin.com");
        outsider.setPassword(passwordEncoder.encode("pass123"));
        outsider.setRole("ADMIN");
        outsider.setStatut("ACTIF");
        outsiderId = userRepository.save(outsider).getId();

        User inactive = new User();
        inactive.setNom("Compte");
        inactive.setPrenom("Inactif");
        inactive.setEmail("inactif@learnhub.com");
        inactive.setPassword(passwordEncoder.encode("pass123"));
        inactive.setRole("ETUDIANT");
        inactive.setStatut("INACTIF");
        userRepository.save(inactive);
    }

    private String getJwtToken(String email, String password) {
        return authService.login(new AuthService.LoginCommand(email, password)).token();
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
                    "sujet": "Question sur les API REST",
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
                    "sujet": "Réponse à votre question",
                    "contenu": "Bonjour Bob, relis la documentation Swagger, tout y est !"
                }
                """;

        String response = mockMvc.perform(post("/api/messages")
                .header("Authorization", "Bearer " + tokenProf)
                .contentType(MediaType.APPLICATION_JSON)
                .content(messageProfJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenu").value("Bonjour Bob, relis la documentation Swagger, tout y est !"))
                .andExpect(jsonPath("$.expediteurId").value(profId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode sentMessage = objectMapper.readTree(response);
        long messageId = sentMessage.get("id").asLong();

        // TEST : L'élève consulte sa boîte de réception (contient la réponse du prof)
        mockMvc.perform(get("/api/messages/recus")
                .header("Authorization", "Bearer " + tokenEleve))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].contenu").value("Bonjour Bob, relis la documentation Swagger, tout y est !"));

        mockMvc.perform(get("/api/messages/non-lus")
                .header("Authorization", "Bearer " + tokenEleve))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nonLus").value(1));

        mockMvc.perform(patch("/api/messages/{id}/lu", messageId)
                .header("Authorization", "Bearer " + tokenEleve))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lu").value(true))
                .andExpect(jsonPath("$.dateLecture").isNotEmpty());

        mockMvc.perform(get("/api/messages/non-lus")
                .header("Authorization", "Bearer " + tokenEleve))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nonLus").value(0));

        mockMvc.perform(get("/api/messages/envoyes")
                .header("Authorization", "Bearer " + tokenProf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].destinataireId").value(eleveId));
    }

    @Test
    void destinatairePeutRepondreMaisUnTiersNePeutPas() throws Exception {
        String tokenEleve = getJwtToken("bob@eleve.com", "pass123");
        String tokenProf = getJwtToken("alice@prof.com", "pass123");
        String tokenOutsider = getJwtToken("chloe@admin.com", "pass123");

        String response = mockMvc.perform(post("/api/messages")
                .header("Authorization", "Bearer " + tokenEleve)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "emailDestinataire": "alice@prof.com",
                            "sujet": "Question",
                            "contenu": "Pouvez-vous m'aider ?"
                        }
                        """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long messageId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(post("/api/messages/{id}/repondre", messageId)
                .header("Authorization", "Bearer " + tokenOutsider)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"contenu\":\"Je ne devrais pas pouvoir répondre.\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/messages/{id}/repondre", messageId)
                .header("Authorization", "Bearer " + tokenProf)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"contenu\":\"Voici la réponse.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sujet").value("Re: Question"))
                .andExpect(jsonPath("$.contenu").value("Voici la réponse."))
                .andExpect(jsonPath("$.expediteurId").value(profId))
                .andExpect(jsonPath("$.destinataireId").value(eleveId));
    }

    @Test
    void annuaireExclutUtilisateurConnecteEtComptesInactifs() throws Exception {
        String tokenProf = getJwtToken("alice@prof.com", "pass123");

        mockMvc.perform(get("/api/messages/destinataires")
                .header("Authorization", "Bearer " + tokenProf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.id == " + eleveId + ")]").exists())
                .andExpect(jsonPath("$[?(@.id == " + outsiderId + ")]").exists())
                .andExpect(jsonPath("$[?(@.email == 'alice@prof.com')]").doesNotExist())
                .andExpect(jsonPath("$[?(@.email == 'inactif@learnhub.com')]").doesNotExist());
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
                .andExpect(status().isUnauthorized());
    }
}
