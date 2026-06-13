package sae.learnhub.learnhub.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnOkWhenUserIsAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateUserAndPrepareInvitation() throws Exception {
        String body = """
                {
                  "nom": "Dupont",
                  "prenom": "Marie",
                  "email": "Marie.Dupont@LearnHub.fr",
                  "password": "Temporaire123!",
                  "role": "PROFESSEUR",
                  "statut": "ACTIF"
                }
                """;

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value("marie.dupont@learnhub.fr"))
                .andExpect(jsonPath("$.user.role").value("PROFESSEUR"))
                .andExpect(jsonPath("$.user.statut").value("ACTIF"))
                .andExpect(jsonPath("$.user.dateCreation").isNotEmpty())
                .andExpect(jsonPath("$.invitationEmailSent").value(false));

        var savedUser = userRepository.findByEmail("marie.dupont@learnhub.fr").orElseThrow();
        assertNotNull(savedUser.getResetToken());
        assertNotNull(savedUser.getResetTokenExpiration());

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("marie.dupont@learnhub.fr"))
                .andExpect(jsonPath("$[0].dateCreation").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRejectDuplicateEmail() throws Exception {
        String body = """
                {
                  "nom": "Dupont",
                  "prenom": "Marie",
                  "email": "marie.dupont@learnhub.fr",
                  "password": "Temporaire123!",
                  "role": "ETUDIANT",
                  "statut": "ACTIF"
                }
                """;

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cette adresse email est déjà utilisée"));
    }
}
