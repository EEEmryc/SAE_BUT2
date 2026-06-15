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
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.IRefreshTokenRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDb() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void adminCreationAllowsLogin() throws Exception {
        String createBody = """
                {
                  "nom": "Zakaria",
                  "prenom": "Test",
                  "email": "zakaria@test.com",
                  "password": "pass12345",
                  "role": "ETUDIANT",
                  "statut": "ACTIF"
                }
                """;

        mockMvc.perform(post("/api/admin/users")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value("zakaria@test.com"));

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "zakaria@test.com",
                                  "password": "pass12345"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn().getResponse().getContentAsString();

        String accessToken = new org.json.JSONObject(loginResponse).getString("token");
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("zakaria@test.com"))
                .andExpect(jsonPath("$.role").value("ETUDIANT"));
    }

    @Test
    void publicRegistrationCannotCreateAccount() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nom": "Direct",
                                  "prenom": "Account",
                                  "email": "direct@test.com",
                                  "password": "pass12345",
                                  "role": "ETUDIANT",
                                  "statut": "ACTIF"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "La création de compte est réservée aux administrateurs. Envoyez une demande d'inscription."));
    }

    @Test
    void testLogout() throws Exception {
        mockMvc.perform(post("/api/admin/users")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nom": "Lu",
                                  "prenom": "To",
                                  "email": "out@t.com",
                                  "password": "pass12345",
                                  "role": "ETUDIANT",
                                  "statut": "ACTIF"
                                }
                                """))
                .andExpect(status().isCreated());

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"out@t.com\",\"password\":\"pass12345\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String refreshToken = new org.json.JSONObject(loginResponse).getString("refreshToken");
        String accessToken = new org.json.JSONObject(loginResponse).getString("token");

        mockMvc.perform(post("/api/auth/logout")
                        .header("X-Refresh-Token", refreshToken)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testForgotPassword() throws Exception {
        User user = createUser("forgot@test.com", "pass");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"forgot@test.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.token").doesNotExist());

        org.junit.jupiter.api.Assertions.assertNotNull(
                userRepository.findByEmail(user.getEmail()).orElseThrow().getResetToken());
    }

    @Test
    void testResetPasswordWithReturnedToken() throws Exception {
        createUser("reset2@test.com", "oldpass");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"reset2@test.com\"}"))
                .andExpect(status().isOk());

        String token = userRepository.findByEmail("reset2@test.com")
                .orElseThrow()
                .getResetToken();

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token":"%s","newPassword":"newPassword123"}
                                """.formatted(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    private User createUser(String email, String password) {
        User user = new User();
        user.setNom("Test");
        user.setPrenom("User");
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ETUDIANT");
        user.setStatut("ACTIF");
        return userRepository.save(user);
    }
}
