package sae.learnhub.learnhub.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sae.learnhub.learnhub.domain.repository.IAccountRequestRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IAccountRequestRepository accountRequestRepository;

    @Autowired
    private IUserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        accountRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void accountRequestCanBeAcceptedThenUsedToCreateTheAccount() throws Exception {
        String requestBody = """
                {
                  "nom": "Martin",
                  "prenom": "Sophie",
                  "email": "Sophie.Martin@example.com",
                  "formation": "BUT Informatique",
                  "requestedRole": "ETUDIANT",
                  "commentaire": "Je souhaite suivre les cours de la plateforme."
                }
                """;

        String response = mockMvc.perform(post("/api/account-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("sophie.martin@example.com"))
                .andExpect(jsonPath("$.statut").value("EN_ATTENTE"))
                .andExpect(jsonPath("$.confirmationEmailSent").value(false))
                .andReturn().getResponse().getContentAsString();

        long requestId = new org.json.JSONObject(response).getLong("id");

        mockMvc.perform(get("/api/admin/account-requests")
                        .param("status", "EN_ATTENTE")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestId))
                .andExpect(jsonPath("$[0].formation").value("BUT Informatique"));

        mockMvc.perform(patch("/api/admin/account-requests/{id}/status", requestId)
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statut\":\"ACCEPTEE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("ACCEPTEE"));

        String createUserBody = """
                {
                  "nom": "Martin",
                  "prenom": "Sophie",
                  "email": "sophie.martin@example.com",
                  "password": "Temporaire123!",
                  "role": "ETUDIANT",
                  "statut": "ACTIF"
                }
                """;

        mockMvc.perform(post("/api/admin/users")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value("sophie.martin@example.com"));
    }

    @Test
    void duplicatePendingRequestIsRejected() throws Exception {
        String body = """
                {
                  "nom": "Dupont",
                  "prenom": "Marie",
                  "email": "marie@example.com",
                  "formation": "Master",
                  "requestedRole": "PROFESSEUR",
                  "commentaire": "Je souhaite proposer des cours sur LearnHub."
                }
                """;

        mockMvc.perform(post("/api/account-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/account-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "Une demande est déjà en attente pour cette adresse email"));
    }

    @Test
    void directPublicRegistrationIsBlocked() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nom": "Direct",
                                  "prenom": "Account",
                                  "email": "direct@example.com",
                                  "password": "password123",
                                  "role": "ETUDIANT",
                                  "statut": "ACTIF"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "La création de compte est réservée aux administrateurs. Envoyez une demande d'inscription."));
    }
}
